import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public class TimeTabling {

    static String directory = "C:/Users/ZAP/Google Drive/KULIAH/OKH/Tugas/UAS/Dataset/Toronto/";
    static String namafile[][] = {	{"car-f-92", "Carlenton92"}, {"car-s-91", "Carlenton91"}, {"ear-f-83", "EarlHaig83"}, {"hec-s-92", "EdHEC92"}, 
									{"kfu-s-93", "KingFahd93"}, {"lse-f-91", "LSE91"}, {"pur-s-93", "pur-s-93"}, {"rye-s-93", "rye-s-93"}, {"sta-f-83", "St.Andrews83"},
									{"tre-s-92", "Trent92"}, {"uta-s-92", "TorontoAS92"}, {"ute-s-92", "TorontoE92"}, {"yor-f-83", "YorkMills83"}};
    
    static String file, filePilihanInput, filePilihanOutput;
    
    static int jumlahexam;
    static int ts;
    static int timeslot[]; // fill with course & its timeslot
    static int conflict_matrix[][]; // fill with conflict matrix
    static int course_degree[][];
    static int jumlahdegree;
    static boolean adaSolusi;
    
	private static Scanner scanner;	
	
	static int max[][];
	static int course_sorted [][];
	
    public static void main(String[] args) throws IOException {
        scanner = new Scanner(System.in);
        for	(int i=0; i< namafile.length; i++)
        	System.out.println(i+1 + ". Penjadwalan " + namafile[i][1]);
        
        System.out.print("\nSilahkan pilih file untuk dijadwalkan : ");
        int pilih = scanner.nextInt();
        
        filePilihanInput = namafile[pilih-1][0];
        filePilihanOutput = namafile[pilih-1][1];
        
        file = directory + filePilihanInput;
        // get course
        getCourse(file);
        
        // set conflict_matrix size with amount of jumlahexam
        conflict_matrix = new int[jumlahexam][jumlahexam];     			
     	//System.out.println("Jumlah Course : " + conflict_matrix.length);
		
        // get student
        getConflictMatrix(file);
		// print dataset array
		/*for (int i=0; i<10; i++) {
			for(int j=0; j<10; j++) {
				System.out.print(conflict_matrix[i][j] + " ");
			}
			System.out.println();
		}*/
		
		// sort exam by degree
		sortingCourse(conflict_matrix, jumlahexam);
		/*for (int i=0; i<jumlahexam; i++)
			System.out.println("Degree of course " + course_sorted[i][0] + " is " + course_sorted[i][1]);*/
		
		// start time
		long starttime = System.nanoTime();
		//timeSloting(conflict_matrix);
		timeSlotingWithSorted(conflict_matrix, course_sorted);
		long endtime = System.nanoTime();
		// end time
		double runningtime = (double) (endtime - starttime)/1000000000;
		
		// print schedule
		printSchedule(filePilihanOutput);
		// print running time
		System.out.println("Waktu eksekusi yang dibutuhkan adalah selama " + runningtime + " detik.");
    }

    private static void getCourse(String file) throws IOException {
        // read course file
		BufferedReader readCourse = new BufferedReader(new FileReader(file + ".crs"));
		while (readCourse.readLine() != null) 
			jumlahexam++; // set how many course taken
		readCourse.close();
    }
    
    private static void getConflictMatrix(String file) throws IOException {
    	// fill dataset array
     	for (int i=0; i<conflict_matrix.length; i++)
     		for(int j=0; j<conflict_matrix.length; j++)
     			conflict_matrix[i][j] = 0;
     	
    	// read student file
		BufferedReader readStudent = new BufferedReader(new FileReader(file + ".stu"));
		String spasi = " ";
		while ((spasi = readStudent.readLine()) != null) {
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
    }
    
    private static void sortingCourse(int[][] conflictmatrix, int jumlahcourse) {
    	course_degree = new int [jumlahexam][2];
		for (int i=0; i<course_degree.length; i++)
			for (int j=0; j<course_degree[0].length; j++)
				course_degree[i][0] = i+1; // fill course_sorted column 1 with course index
		
    	for (int i=0; i<jumlahcourse; i++) {
			for (int j=0; j<jumlahcourse; j++)
				if(conflictmatrix[i][j] > 0)
					jumlahdegree++;
					else
						jumlahdegree = jumlahdegree;					
			course_degree[i][1] = jumlahdegree; // fill amount of degree for each course
			jumlahdegree=0;
		}
    	// sorting by degree
    	max = new int[1][2]; // make max array with 1 row 2 column. untuk ngehandle degree
    	max[0][0] = -1;
		max[0][1] = -1;
		int x = 0;
		course_sorted = new int[jumlahexam][2];
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
    }
      
    private static void timeSloting(int[][] conflictmatrix) {
    	timeslot = new int[jumlahexam];
    	ts = 1;
    	for(int i= 0; i < conflictmatrix.length; i++)
    		timeslot[i] = 0;
    	
    	
		for(int i = 0; i < conflictmatrix.length; i++) {
			for (int j = 1; j <= ts; j++) {
				if(isTimeslotAvailable(i, j, conflictmatrix, timeslot)) {
					timeslot[i] = j;
					break;
				}
					else
						ts = ts+1;
			}
		}
    }
    private static void timeSlotingWithSorted(int[][] conflictmatrix, int [][] sortedCourse) {
    	timeslot = new int[jumlahexam];
    	ts = 1; // starting timeslot from 1
    	for(int i= 0; i < sortedCourse.length; i++)
    		timeslot[i] = 0;
    	
    	
		for(int i = 0; i < sortedCourse.length; i++) {
			for (int j = 1; j <= ts; j++) {
				if(isTimeslotAvailableWithSorted(i, j, conflictmatrix, sortedCourse, timeslot)) {
					timeslot[i] = j;
					break;
				}
					else
						ts = ts+1;
			}
		}
    }
    
    public static boolean isTimeslotAvailable(int course, int timeslot, int[][] conflictmatrix, int[] timeslotarray) {
		for(int i = 0; i < conflictmatrix.length; i++)
			if(conflictmatrix[course][i] != 0 && timeslotarray[i] == timeslot)
				return false;
		
		return true;
	}
    public static boolean isTimeslotAvailableWithSorted(int course, int timeslot, int[][] conflictmatrix, int[][] sortedmatrix, int[] timeslotarray) {
		for(int i = 0; i < sortedmatrix.length; i++) 
			if(conflictmatrix[sortedmatrix[course][0]-1][i] != 0 && timeslotarray[i] == timeslot)
				return false;
		
		return true;
	}
    /*private static void timesloting(int[][] conflictmatrix, int jumlah_timeslot) { 
        timeslot = new int[jumlahexam]; 
        for (int i = 0; i < jumlahexam; i++) 
            timeslot[i] = 0; 
  
        if (isAdaSolusi(conflictmatrix, jumlah_timeslot, timeslot, 0))
        	adaSolusi = true;
        	else
        		adaSolusi = false;
    }
    
    private static boolean checkTimeSlot(int course, int[][] conflictmatrix, int[] timeslotarray, int timeslot) {
    	for (int i = 0; i < jumlahexam; i++) {
            if (conflictmatrix[course][i] == 1 && timeslotarray[i] == timeslot) // check if crash or not
                return false;
    	}
    	return true;
    } 
    
    private static boolean isAdaSolusi(int[][] conflictmatrix, int jumlah_timeslot, int[] timeslot, int course) { 
        if (course == jumlahexam) 
            return true; 
  
        for (int i = 1; i <= jumlah_timeslot; i++) { 
            if (checkTimeSlot(course, conflictmatrix, timeslot, i)) { 
                timeslot[course] = i;
 
                if (isAdaSolusi(conflictmatrix, jumlah_timeslot, timeslot, course+1)) 
                    return true; 
  
                timeslot[course] = 0; 
            } 
        } 
  
        return false; 
    }*/
    
    public static void printSchedule(String file) { 
    	System.out.println("\n================================================\n");
    	for (int i = 0; i < jumlahexam; i++)
    		System.out.println("Timeslot untuk course "+ (i+1) +" adalah timeslot: " + timeslot[i]);       
	    
    	System.out.println("\n================================================"); 
	    System.out.println("Jumlah timeslot yang dibutuhkan untuk menjadwalkan " + jumlahexam + " course di file " + file + " adalah "+ Arrays.stream(timeslot).max().getAsInt() + " timeslot.");
    }
    
    public static void writeSolFile(int[][]jadwal, String namaFile) {
		try {
        FileWriter writer = new FileWriter(namaFile+".sol", true);
        for (int i = 0; i <jadwal.length; i++) {
            for (int j = 0; j <jadwal[i].length; j++) {
                  writer.write(jadwal[i][j]+ " ");
            }
            //this is the code that you change, this will make a new line between each y value in the array
            writer.write("\n");   // write new line
        }
        writer.close();
		} 
			catch (IOException e) {
				e.printStackTrace();
			}
	}
}