#include <stdio.h>
#include <string.h>
#include <mpi.h>
#include <stdlib.h>
#include <time.h>

/* Number of elements for every process */
int k = 4;
    
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
int subTotal(int numbers[]){
	int total = 0, i;
	
	for(i = 0; i < k; i++)		
		total += numbers[i];
	
	return total; 
}

int main( int argc, char* argv[] )
{	
	int			my_rank;		/* rank of process 				*/
    int			p;				/* number of processes 			*/
    int			sum = 0;		/* sum of array					*/
    int 		i;				/* counter 						*/
	MPI_Status	status;			/* return status for receive	*/
    int err;

	/* Start up MPI */
    err = MPI_Init( &argc, &argv );

	/* Find out process rank */
	MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);

	/* Find out number of processes */
	MPI_Comm_size(MPI_COMM_WORLD, &p);
	
	
	int total[p]; // Sum of subarrays
	int ARRAY_SIZE = p * k; // Total array size
	int	numbers[ARRAY_SIZE]; // Numbers array	
	
	/* random numbers array	*/
	randomNumbers(numbers, ARRAY_SIZE);
	
	/* Scatter */
	MPI_Scatter (numbers, k, MPI_INT, numbers,
              ARRAY_SIZE, MPI_INT, 0, MPI_COMM_WORLD); 
    
    /* Sum of subarray */
    sum = subTotal(numbers);
    
    printf("I am %d. process, partial sum = %d\n", my_rank, sum);       
	
	/* Gather */
	MPI_Gather (&sum, 1, MPI_INT, total,
	  1, MPI_INT, 0, MPI_COMM_WORLD); 
	
	if(my_rank == 0) {
		/* Master*/
		
		/* Sum of array */
		sum = 0;
		for(i=0;i<p;i++)
			sum += total[i];
		
		printf("Total : %d\n",sum);	
		
		printf("Numbers: [ ");
		for(i=0; i<ARRAY_SIZE; i++)	
			printf("%d, ",numbers[i]);
		printf("]\n");
	} 

	/* Shut down MPI */
    err = MPI_Finalize();
    return 0;
}
