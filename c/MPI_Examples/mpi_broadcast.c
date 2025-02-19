#include <stdio.h>
#include <string.h>
#include <mpi.h>
#include <stdlib.h>
#include <time.h>

/* p = n*k ; p: process, n: numbers */
int k = 10;
    
/* Create random numbers array */
void randomNumbers(int numbers[], int size){
	
	int number, i;
	srand(time(NULL));
	
	for(i=0; i<size; i++){
		number = 1 + (rand() % 99);
		numbers[i] = number;
	} 
} 

/* Sum of subarray */
int subTotal(int numbers[], int rank){
	int total = 0, i;
	int from = rank*k, to = from + k;
	
	for(i = from; i < to; i++)
		total += numbers[i];
	
	return total; 
}

int main( int argc, char* argv[] )
{
	
    int			my_rank;		/* rank of process 				*/
    int 		tag = 0;		/* tag for messages				*/
    int			p;				/* number of processes 			*/
    int 		subTotals = 0;  /* sum of subarray				*/
    int			total = 0;		/* sum of array					*/
    int			totals = 0;		/* sum of subarrays				*/
    MPI_Status		status;			/* return status for receive	*/
    int err;

	/* Start up MPI */
    err = MPI_Init( &argc, &argv );

	/* Find out process rank */
	MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);

	/* Find out number of processes */
	MPI_Comm_size(MPI_COMM_WORLD, &p);
	
	int ARRAY_SIZE = p*k;
	int	numbers[ARRAY_SIZE];	
	
	/* random numbers array	*/
	randomNumbers(numbers, ARRAY_SIZE);
	
	if(my_rank == 0) {
		/* Master*/
		int i;
		
		/* Broadcast */
		MPI_Bcast(numbers, ARRAY_SIZE, MPI_INT, 0, MPI_COMM_WORLD);
		
		/* Sum of subbarrays*/
		for(i=1; i<p; i++){
			/* Receive from slaves */ 
			MPI_Recv(&totals, 1, MPI_INT, i, tag, 
						MPI_COMM_WORLD, &status);
			total += totals;
		}
		
		/* Sum of master's array */
		subTotals = subTotal(numbers, my_rank);
		printf("Subtotal : %d (I am master(%d))\n",subTotals, my_rank);
		
		/* Sum of array */
		total += subTotals;
		printf("Total : %d\n",total);
		
		printf("Numbers: [ ");
		for(i=0; i<ARRAY_SIZE; i++)	
			printf("%d, ",numbers[i]);
		printf("]\n");
		
	} else { /* Slaves */
	
			/* Sum of subarray */
			subTotals = subTotal(numbers, my_rank);
			
			printf("Subtotal : %d (I am slave %d)\n",subTotals, my_rank);
			
			/* Sent to master */	
			MPI_Send(&subTotals, 1, MPI_INT, 0, tag, 
					MPI_COMM_WORLD);
			
	}

	/* Shut down MPI */
    err = MPI_Finalize();
    return 0;
}
