import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

/**
 * Three algorithms to solve the Canoe Rental problem, one brute force, one
 * divide and conquer, and one dynamic.
 * @author Andrew Gates and Brandon Watt
 * @version 1
 */
public class tcss343
{
	/** The number of rows in the input.*/
	public static final int N = 5;
	/** The max value that the random integer in the input can be.*/
	public static final int BOUND = 20;

	/**
	 * Drives the program.
	 * @param theArgs
	 */
	public static void main(String[] theArgs)
	{
		// Asks if a new file should be generated for testings.
		// Uses the old file if a new on is not made.
		System.out.println("Generate new file? (y/n)");
		Scanner decision = new Scanner(System.in);
		if (decision.next().charAt(0) == 'y')
		{
			fileGenerator();
		}
		decision.close();

		// Sets up the file to be read from.
		String filename = "sample_input.txt";
		File file = new File("./" + filename);

		try
		{
			int columns = 0;

			// Scans the first line of the input.
			Scanner scan = new Scanner(file);
			String input = scan.nextLine();

			// Breaks apart the first line into it's individual value.
			String[] values = input.split("\t");
			// Determines the number of columns in the table.
			columns = values.length;
			scan.close();

			Scanner read = new Scanner(file);

			// Creates a 2D array to be filled with the tables values.
			int[][] table = new int[columns][columns];

			int rows = 0;
			// Reads in the full file.
			while(read.hasNext())
			{
				input = read.nextLine();
				rows++;
				// Breaks apart the line into it's individual values.
				values = input.split("\t");

				// Go through the values an put them in the table.
				for(int i = 0; i < values.length; i++)
				{
					// If the value is NA.
					if (values[i].equals("NA"))
					{
						table[rows - 1][i] = Integer.MAX_VALUE;
					}
					else
					{
						table[rows - 1][i] = Integer.valueOf(values[i]).intValue();
					}
				}
				scan.close();
			}

			read.close();

			// Call the three algorithms.
			recursiveHelperBF(table);
			recursiveHelperDAC(table);
			dynamic(table);

		} catch (FileNotFoundException e)
		{
			// If file is not found.
			e.printStackTrace();
		}

	}

	/**
	 * Creates the arrays and variables that are used in recursiveHelper. It will initialize the
	 * minimum value and the route path for that value before the for loop. Then it will use the for
	 * loop to iterate from left to right of the top row of the table, calculating the minimum value
	 * for each index and then comparing it to the previous minimum value. If the new value is
	 * smaller it will assign the minimum to be that value, and assign the route to be the route of
	 * the new minimum. Reversing the route to be in increasing order and then printing it in the end.
	 * @param theTable A table filled with the costs of traveling from i to j.
	 */
	public static void recursiveHelperBF(final int[][] theTable)
	{
		int sum = 0, reversalIndex = 0, min = theTable[0][theTable.length - 1];

		//Route arrays to be updated when calculating the path for each traversal option.
		int[] route = new int[theTable.length];
		int[] tempRoute = new int[theTable.length];
		int[] tempRoute2 = new int[theTable.length];
		int[] reversalRoute = new int[theTable.length];

		//Setting the initial minimum route to be the route of the top right element.
		route[theTable.length - 1] = 1;
		route[theTable.length - 2] = theTable.length;

		//For loop to traverse from left to right of the top row of the table.
		for(int shift = 1; shift < theTable.length; shift++)
		{
			//Resetting the arrays back to 0 to avoid old data.
			for(int h = 0; h < route.length; h++)
			{
				tempRoute[h] = 0;
				tempRoute2[h] = 0;
			}

			//Initializing the route to be the current index that will be traversed.
			tempRoute[theTable.length - 1] = 1;
			tempRoute[theTable.length - 2] = shift + 1;

			//Adding the current index to the sum, and then adding the minimum result from recursiveBF.
			sum += theTable[0][shift];
			sum += recursiveBF(theTable, 0, theTable.length - 3, 0, min, shift, tempRoute, tempRoute2);

			//If the new sum is smaller than min, reassign min to that value and update it's route.
			if(sum < min)
			{
				min = sum;
				for(int i = 0; i < route.length; i++)
				{
					route[i] = tempRoute2[i];
				}
			}
			sum = 0;
		}

		//Reverse the route to be in increasing order.
		for(int j = route.length - 1; j >= 0; j--)
		{
			reversalRoute[reversalIndex] = route[j];
			reversalIndex++;
		}

		System.out.println("Min is - " + min);
		System.out.println(Arrays.toString(reversalRoute));
	}

	/**
	 * Method to be used with recursiveHelperBF that will make recursive calls to calculate the current
	 * minimum and update it's route, finally returning the minimum of the current call.
	 * @param theTable A table filled with the costs of traveling from i to j.
	 * @param i The index of the current row.
	 * @param j The index top update tempRoute.
	 * @param sum The current sum of the recursive calls.
	 * @param min The current min.
	 * @param shift The current row index.
	 * @param tempRoute The route of the current sum.
	 * @param tempRoute2 The minimum route within recursiveBF.
	 * @return Returns the current within recursiveBF.
	 */
	private static int recursiveBF(final int[][] theTable, int i, int j,
									int sum, int min, int shift, int[] tempRoute, int[] tempRoute2)
	{

		int x = theTable.length - 1;

		//If the current index is out of bounds check the sum against the min and update if it is less.
		if(shift >= theTable.length - 1 || i > theTable.length - 1)
		{
			if(sum < min)
			{
				min = sum;

				for(int k = 0; k < tempRoute.length; k++)
				{
					tempRoute2[k] = tempRoute[k];
				}

			}
		}

		else
		{
			//Iterate from the right most index left until you reach a 0.
			while(theTable[shift][x] != 0)
			{
				tempRoute[j] = x + 1;
				min = recursiveBF(theTable, i + 1, j - 1, sum + theTable[shift][x], min, x, tempRoute, tempRoute2);
				x--;
			}
		}
		return min;
	}

	/**
	 * Calls the Divide and Conquer recursive algorithm.
	 * @param theTable  A table filled with the costs of traveling from i to j.
	 */
	public static void recursiveHelperDAC(final int[][] theTable)
	{
		// Sets up the two arrays used to determine the route.
		int[] route = new int[theTable.length + 1];
		Arrays.fill(route, Integer.MAX_VALUE);
		int[] tempRoute = new int[theTable.length + 1];
		Arrays.fill(tempRoute, N);

		// Calls the recursive function.
		int min = recursiveDAC(theTable, 0, 1, 0, Integer.MAX_VALUE, route, tempRoute, 0);

		// Prints the results.
		System.out.println("Min is - " + min);
		System.out.println(Arrays.toString(route));
	}

	/**
	 * Travels through the table recursively finding the cheapest path, and returning
	 * it's cost and route.
	 * @param theTable  A table filled with the costs of traveling from i to j.
	 * @param i The index of the current row.
	 * @param j The index of the current column.
	 * @param sum The current total sum.
	 * @param min The cost of the cheapest found so far.
	 * @param route The path of the cheapest route found so far.
	 * @param tempRoute The current path traveled.
	 * @param pos The index of the route.
	 * @return The value of the cheapest route.
	 */
	private static int recursiveDAC(final int[][] theTable, int i, int j,
									int sum, int min, int[] route, int[] tempRoute, int pos)
	{
		// If the current position isn't the last row or last column in the table.
		if (i < theTable.length - 1 && j < theTable.length - 1)
		{
			// Update the current route.
			tempRoute[0] = 1;
			tempRoute[pos+1] = j + 1;

			// Return the minimum route cost of one route that trades in the canoe and the other route which
			// continues to the next location.
			return Math.min(recursiveDAC(theTable, j, j + 1, sum + theTable[i][j], min, route, tempRoute, pos+1),
							recursiveDAC(theTable, i, j + 1, sum, min, route, tempRoute, pos));
		}
		else
		{
			// Add to the sum.
			sum += theTable[i][j];

			// Update the route.
			tempRoute[pos+1] = j + 1;
			// Store the sum of this route in the last element of the array.
			tempRoute[theTable.length] = sum;

			// If the sum is less than the min.
			if (sum < min)
			{
				// The sum is the new min.
				min = sum;
				// If this is the cheapest current route.
				if (tempRoute[theTable.length] < route[theTable.length])
				{
					// Change the route so that it is the new smallest.
					for(int k = 0; k < route.length; k++)
					{
						route[k] = tempRoute[k];
					}
				}
			}
			return sum;
		}
	}


	/**
	 * A dynamic algorithm that iterates through the table, storing the smallest path
	 * values and printing out the value of the smallest path and it's route.
	 * @param theTable A table filled with the costs of traveling from i to j.
	 * @author Brandon Watt
	 */
	public static void dynamic(final int[][] theTable)
	{
		PriorityQueue<Integer> list = new PriorityQueue<Integer>();

		// A 2D array whose values represents the smallest possible
		// cost required to get to that location.
		int[][] summations = new int[theTable.length][theTable.length];

		// Keeps track of the row where the cheapest path occurred.
		int least = 0;

		// Fills the first row of the array with the values from the theTable.
		for (int j = 0; j < theTable.length; j++)
		{
			summations[0][j] = theTable[0][j];
		}
		// Adds the 1->final position value to the heap.
		list.add(Integer.valueOf(summations[0][theTable.length - 1]));

		// A double for loop that traverses the values in the theTable.
		for(int i = 1; i < theTable.length - 1; i++)
		{
			for(int j = i + 1; j < theTable.length; j++)
			{
				// Finds the smallest value of the appropriate row.
				int min = Integer.MAX_VALUE;
				for(int k = i; k > 0; k--)
				{
					if ((summations[k-1][i]) < min)
					{
						min = summations[k-1][i];
					}
				}
				// Puts the cheapest possible cost of the (i,j) position in
				// the summations array at the (i,j) position.
				summations[i][j] = theTable[i][j] + min;
			}

			// Adds the value to the heap, which will determine if it is the
			// cheapest path.
			list.add(Integer.valueOf(summations[i][theTable.length - 1]));

			// If it is the cheapest path least is moved to mark the row it's in.
			if (list.peek().intValue() >= summations[i][theTable.length -1])
			{
				least = i+1;
			}
		}

		// The array that displays the route traveled.
		int[] route = new int[theTable.length];
		route[theTable.length-1] = theTable.length;
		route[0] = 1;

		// Constructs the array by backtracking through the 2D array, using the
		// least value as a marker for the cheapest path.
		for(int k = theTable.length - 2; k > 0; k--)
		{
			int min = 0;
			int position = 0;
			for(int l = least -1; l >0; l--)
			{
				min = Integer.MAX_VALUE;
				if(summations[l][least] < min)
				{
					min = summations[l][least];
					position = l;
				}
			}
			route[k] = least;
			least = position;
		}

		// A makes a new array without the extra zeros.
		int[] finished = new int[theTable.length];
		int shift = 0;
		for(int i = 0; i < theTable.length; i++)
		{
			if(i+shift < theTable.length)
			{
				if (route[i+shift] == 0)
				{
					shift++;
					i--;
				}
				else
				{
					finished[i] = route[i+shift];
				}
			}
		}

		// Prints out the minimum and the route taken.
		System.out.println("Min is - " + list.peek());
		System.out.println(Arrays.toString(finished));
	}

	/**
	 * Creates a new table of N rows and N columns in a file.
	 * Values below the diagonal (i,i) will be random values between 1-BOUNDS.
	 * Values above the diagonal (i,i) will be "NA".
	 * Values on the diagonal will be 0.
	 */
	public static void fileGenerator()
	{
		Random generator = new Random();

		try
		{
			PrintWriter out = new PrintWriter("sample_input.txt");

			for (int i = 0; i < N; i++)
			{
				for (int j = 0; j < N; j++)
				{
					if ( i == j)
					{
						//System.out.print("0\t");
						out.print("0\t");
					}
					else if (i > j)
					{
						//System.out.print("NA\t");
						out.print("NA\t");
					}
					else
					{
						int input = generator.nextInt(BOUND) + 1;
						//System.out.print(input + "\t");
						out.print(input + "\t");
					}
				}

				//System.out.print("\n");
				out.print("\n");
			}

			out.close();
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
