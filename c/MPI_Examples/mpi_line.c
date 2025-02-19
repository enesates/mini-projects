#include <stdio.h>
#include <string.h>
#include <mpi.h>

int main( int argc, char* argv[] )
{
	int			my_rank;			/* rank of process 				*/
	int 		p;					/* number of processes 			*/
	int 		source = 0;			/* rank of source 				*/
	int 		dest = 0;			/* rank of receiver				*/
	int 		tag = 0;			/* tag for messages				*/
	char		message[100];		/* another messages				*/
	char		masterMessage[100];	/* user message					*/
	MPI_Status		status;				/* return status for receive	*/
	int err;
    
	
	/* Start up MPI */
	err = MPI_Init( &argc, &argv );

	/* Find out process rank */
	MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);

	/* Find out number of processes */
	MPI_Comm_size(MPI_COMM_WORLD, &p);

	if(my_rank == 0) {
		
		/* Master*/
		if(source == 0) {
			puts("Enter the message: ");
			gets(masterMessage);		
			puts("I am master! I am starting the game...");
		}
		
		/* If there is one process. */
		if (p == 1)
			dest = my_rank;
		else
			dest = my_rank + 1;
		/* Destination is next process: first slave. */
		
		MPI_Send(masterMessage, strlen(message)+1, MPI_CHAR, dest, tag, 
				MPI_COMM_WORLD);
		
		/* If there is one process. */
		if(p == 1)
			source = my_rank;
		else
			source = my_rank + 1;
		/* Source is next process. Because it's line topology. */
		
		MPI_Recv(message, 100, MPI_CHAR, source, tag, 
					MPI_COMM_WORLD, &status);
		printf("\nI am master (%d), I received: %s\nBen bu oyunu bozarim.\n\n", my_rank,message);
		/* Last message */
			
		
	} else { /* Slaves */
		
			/* Source is previous process. */
			source = my_rank - 1;
				
			MPI_Recv(message, 100, MPI_CHAR, my_rank-1, tag, 
						MPI_COMM_WORLD, &status);
			printf("\nI am slave %d, I received: %s", my_rank,message);
			
			
			/* Destination is next process, if it isn't last process.*/
			if(my_rank != p-1){	
				printf("\nI am slave %d, I sent: %s\n", my_rank, message);
				
				dest = my_rank + 1;
				MPI_Send(message, strlen(message)+1, MPI_CHAR, dest, tag, 
					MPI_COMM_WORLD);	
			}
			
			
			/* Source is next process. Because direction is back.*/
			if(my_rank != p-1){
				
				source = my_rank + 1;
				MPI_Recv(message, 100, MPI_CHAR, source, tag, 
						MPI_COMM_WORLD, &status);
						
				printf("\nI am slave %d, I received: %s", my_rank,message);
			}
			
			
			/* Destination is previous process. Because direction is back. */	
			printf("\nI am slave %d, I sent: %s\n", my_rank, message);
			
			dest = my_rank - 1;
			MPI_Send(message, strlen(message)+1, MPI_CHAR, dest, tag, 
					MPI_COMM_WORLD);
			
	}

	/* Shut down MPI */
    err = MPI_Finalize();
    return 0;
}
