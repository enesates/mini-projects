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
	MPI_Status	status;				/* return status for receive	*/
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
			source = p-1;
		/* Source is last process. Because it's ring topology. */
		
		MPI_Recv(message, 100, MPI_CHAR, source, tag, 
					MPI_COMM_WORLD, &status);
		printf("\nI am master (%d), I received: %s\nBen bu oyunu bozarim.\n\n", my_rank,message);
		/* Last message */
			
		
	} else { /* Slaves */
		
			/* Source is previous process. */
			source = my_rank - 1;
			MPI_Recv(message, 100, MPI_CHAR, source, tag, 
						MPI_COMM_WORLD, &status);
			printf("\nI am slave %d, I received: %s", my_rank,message);
			/* Received */
			
			/* Sent */	
			printf("\nI am slave %d, I sent: %s\n", my_rank, message);
			
			/* Destination is next process. */
			dest = (my_rank+1)%p;
			MPI_Send(message, strlen(message)+1, MPI_CHAR, dest, tag, 
					MPI_COMM_WORLD);
			
	}

	/* Shut down MPI */
    err = MPI_Finalize();
    return 0;
}
