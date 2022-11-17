//
// To compile this Java program, type:
//
//	javac GlobalAlignment.java
//
// To run the program, type:
//
//	java GlobalAlignment
//


public class Levenshtein {

	public static final int MAX_LENGTH	= 100;

	public static final int MATCH_SCORE	= 2;
	public static final int MISMATCH_SCORE	= -1;
	public static final int GAP_PENALTY	= -2;

	public static final int STOP		= 0;
	public static final int UP		= 1;
	public static final int LEFT		= 2;
	public static final int DIAG		= 3;

    public static void main(String[] args) {

	int i, j;
	int alignmentLength, score, tmp;

	String X = "ATCGAT";
	String Y = "ATACGT";

	int F[][] = new int[MAX_LENGTH+1][MAX_LENGTH+1];     /* score matrix */
	int trace[][] = new int[MAX_LENGTH+1][MAX_LENGTH+1]; /* trace matrix */	
	char[] alignX = new char[MAX_LENGTH*2];	/* aligned X sequence */
	char[] alignY = new char[MAX_LENGTH*2];	/* aligned Y sequence */

	int m = X.length();
	int n = Y.length();


	//
	// Initialise matrices
	//

	F[0][0] = 0;
	trace[0][0] = STOP;
	for ( i=1 ; i<=m ; i++ ) {
		F[i][0] = F[i-1][0] + GAP_PENALTY;
		trace[i][0] = STOP;
	}
	for ( j=1 ; j<=n ; j++ ) {
		F[0][j] = F[0][j-1] + GAP_PENALTY;
		trace[0][j] = STOP;
	}


	//
	// Fill matrices
	//

	for ( i=1 ; i<=m ; i++ ) {

		for ( j=1 ; j<=n ; j++ ) {

			if ( X.charAt(i-1)==Y.charAt(j-1) ) {
				score = F[i-1][j-1] + MATCH_SCORE;
			} else {
				score = F[i-1][j-1] + MISMATCH_SCORE;
			}
			trace[i][j] = DIAG;

			tmp = F[i-1][j] + GAP_PENALTY;
			if ( tmp>score ) {
				score = tmp;
				trace[i][j] = UP;
			}

			tmp = F[i][j-1] + GAP_PENALTY;
			if( tmp>score ) {
				score = tmp;
				trace[i][j] = LEFT;
			}

			F[i][j] = score;
		}
	}


	//
	// Print score matrix
	//

	System.out.println("Score matrix:");
	System.out.print("      ");
	for ( j=0 ; j<n ; ++j ) {
		System.out.print("    " + Y.charAt(j));
	}
	System.out.println();
	for ( i=0 ; i<=m ; i++ ) {
		if ( i==0 ) {
			System.out.print(" ");
		} else {
			System.out.print(X.charAt(i-1));
		}
		for ( j=0 ; j<=n ; j++ ) {
			System.out.format("%5d", F[i][j]);
		}
		System.out.println();
	}
	System.out.println();


	//
	// Trace back from the lower-right corner of the matrix
	//

	i = m;
	j = n;
	alignmentLength = 0;

	while ( trace[i][j] != STOP ) {

		switch ( trace[i][j] ) {

			case DIAG:
				alignX[alignmentLength] = X.charAt(i-1);
				alignY[alignmentLength] = Y.charAt(j-1);
				i--;
				j--;
				alignmentLength++;
				break;

			case LEFT:
				alignX[alignmentLength] = '-';
				alignY[alignmentLength] = Y.charAt(j-1);
				j--;
				alignmentLength++;
				break;

			case UP:
				alignX[alignmentLength] = X.charAt(i-1);
				alignY[alignmentLength] = '-';
				i--;
				alignmentLength++;
		}
	}


	//
	// Unaligned beginning
	//

	while ( i>0 ) {
		alignX[alignmentLength] = X.charAt(i-1);
		alignY[alignmentLength] = '-';
		i--;
		alignmentLength++;
	}

	while ( j>0 ) {
		alignX[alignmentLength] = '-';
		alignY[alignmentLength] = Y.charAt(j-1);
		j--;
		alignmentLength++;
	}

	// Print alignment
	printAlignment(alignmentLength, alignX, alignY);
	    
	System.out.println("Percent Identity is: " + pid(alignmentLength, alignX, alignY));

	System.out.println("Hamming Distance is: " + hamming(alignmentLength, alignX, alignY)); 

	System.out.println("Levenshtein Distance is: " + levenshtein(alignmentLength, alignX, alignY));

}

/* 
 *Exercise 1
 */
static void printAlignment(int aLength, char[] alignX, char[] alignY){
	System.out.println("Optimal Local Alignment: ");
	for (int i = aLength-1 ; i>=0 ; i-- ) {
		System.out.print(alignX[i]);
	}

	System.out.println();
	//Simple loop which checks for a dash in the X or Y character. If found in either, it prints a "|".
	for (int i = aLength-1 ; i>=0 ; i-- ) {
		if((alignX[i] != '-' &&  alignY[i] != '-') && alignX[i] == alignY[i]){
			System.out.print('|');
		}
		else{
			System.out.print(' ');
		}
	}
	System.out.println();

	for (int i=aLength-1 ; i>=0 ; i-- ) {
		System.out.print(alignY[i]);
	}
	System.out.println();
}

/*
 * Exercise 2
 */
static int pid(int aLength, char[] alignX, char[] alignY){
	int identity = 0;
	int count = 0;
	for (int i = aLength-1 ; i>=0 ; i--) {
		if(alignX[i] == (alignY[i])){
			count += 1;
		}
	}
	//Divides by shortest of the 2 sequences.
	if(alignX.length <= alignY.length) identity = (count*100) / alignX.length; 

	else identity = (count*100) / alignY.length; 
	
	return identity;
}

/*
 * Exercise 3
 */
static int hamming(int aLength, char[] alignX, char[] alignY){
	int dist = 0;
	for (int i = 0; i < aLength; i++) {
		if(alignX[i] != alignY[i]){
			dist += 1;
		}
	}
	return dist;
}

/*
 * Exercise 5
 */
static int levenshtein(int aLength, char[] x, char[] y){
	int changes = 0;
	for (int i = 0; i < aLength; i++) {
		
		if(i == 0 && x[i] != y[i]){ //Checks first character in both
			// if next chars are same (replace), or char in x is same as char in next pos of y (add), or char in x isn't same as char in next pos of y (remove)
			if(x[i+1] == y[i+1] || x[i] == y[i+1] || x[i] != y[i+1]) changes += 1;
		}
		if(i != aLength-1 && i != 0 && x[i] != y[i]){ //Checks characters between first and last in both strings
			// if next and last chars are same (replace), or /-/....
			if(x[i+1] == y[i+1] && x[i-1] == y[i-1] || x[i] == y[i+1] || x[i] != y[i+1]) changes += 1;
		}
		if(i == aLength-1 && x[i] != y[i]){ //Checks last character in both strings
			// if next and last chars are same (replace), or /-/....
			if(x[i-1] == y[i-1] || x[i] == y[i+1] || x[i] != y[i+1]) changes += 1;
		}
	}
	return changes;
}
}
