import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Course {
	
	int jumlahmurid;

	String fileInput;
	
	public Course(String fileInput) { this.fileInput = fileInput; }
	
	public int getJumlahMurid() { return this.jumlahmurid; }
	public int getJumlahCourse() throws IOException {
		int jumlahcourse = 0;
		// read course file
		BufferedReader readCourse = new BufferedReader(new FileReader(fileInput + ".crs"));
			while (readCourse.readLine() != null) 
				jumlahcourse++; // set how many course taken
		readCourse.close();
		
		return jumlahcourse;
	}
	
	public int[][] getConflictMatrix() throws IOException {
		// fill dataset array
		int[][] conflict_matrix = new int[getJumlahCourse()][getJumlahCourse()];
     	for (int i=0; i<conflict_matrix.length; i++)
     		for(int j=0; j<conflict_matrix.length; j++)
     			conflict_matrix[i][j] = 0;
     	
    	// read student file
		BufferedReader readStudent = new BufferedReader(new FileReader(fileInput + ".stu"));
		String spasi = " ";
		while ((spasi = readStudent.readLine()) != null) {
			jumlahmurid++;
			String tmp [] = spasi.split(" ");
			if	(tmp.length > 1) {
				for(int i=0; i<tmp.length; i++)
					for(int j=i+1; j<tmp.length; j++) {
						conflict_matrix[Integer.parseInt(tmp[i])-1][Integer.parseInt(tmp[j])-1]++;
						conflict_matrix[Integer.parseInt(tmp[j])-1][Integer.parseInt(tmp[i])-1]++;
				}
			}
		}
		readStudent.close();	
		
		return conflict_matrix;
	}
	
	public int [][] sortingByDegree(int[][] conflictmatrix, int jumlahcourse) {
		int[][] course_degree = new int [jumlahcourse][2];
		int degree = 0;
		for (int i=0; i<course_degree.length; i++)
			for (int j=0; j<course_degree[0].length; j++)
				course_degree[i][0] = i+1; // fill course_sorted column 1 with course index
		
    	for (int i=0; i<jumlahcourse; i++) {
			for (int j=0; j<jumlahcourse; j++)
				if(conflictmatrix[i][j] > 0)
					degree++;
					else
						degree = degree;					
			course_degree[i][1] = degree; // fill amount of degree for each course
			degree=0;
		}
    	// sorting by degree
    	int[][] max = new int[1][2]; // make max array with 1 row 2 column. untuk ngehandle degree
    	max[0][0] = -1;
		max[0][1] = -1;
		int x = 0;
		int[][] course_sorted = new int[jumlahcourse][2];
		
		for(int a=0; a<course_degree.length; a++) {
			for(int i=0; i<course_degree.length; i++) {
				if(max[0][1]<course_degree[i][1]) {
					max[0][0] = course_degree[i][0];
					max[0][1] = course_degree[i][1];
					x = i;
				}				
			}
			course_degree[x][0] = -2;
			course_degree[x][1] = -2;
			course_sorted[a][0] = max[0][0];
			course_sorted[a][1] = max[0][1];
			max[0][0] = -1;
			max[0][1] = -1;
		
		}
		return course_sorted;
	}
	
	/*public int[][] sortingBySaturation(int[][] conflictmatrix, int jumlahcourse) {
		course_sorted = new int[jumlahcourse][2];
		
		return course_sorted;
	}*/
}
