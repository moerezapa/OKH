import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TimeTabling {

    static String folderDataset = "C:/Users/ZAP/Google Drive/KULIAH/OKH/Tugas/UAS/Dataset/Toronto/";
    static String namafile[][] = {	{"car-f-92", "Carleton92"}, {"car-s-91", "Carleton91"}, {"ear-f-83", "EarlHaig83"}, {"hec-s-92", "EdHEC92"}, 
									{"kfu-s-93", "KingFahd93"}, {"lse-f-91", "LSE91"}, {"pur-s-93", "pur93"}, {"rye-s-93", "rye92"}, {"sta-f-83", "St.Andrews83"},
									{"tre-s-92", "Trent92"}, {"uta-s-92", "TorontoAS92"}, {"ute-s-92", "TorontoE92"}, {"yor-f-83", "YorkMills83"}
								};
    
    static String file, filePilihanInput, filePilihanOutput;
    
    static int jumlahexam;
    
    static int timeslot[]; // fill with course & its timeslot
    static int conflict_matrix[][]; // fill with conflict matrix	
	
	static int course_sorted [][];
	static int hasil_timeslot[][];
	
	private static Scanner scanner;
	private static Course course;
	private static Schedule schedule;
	
    public static void main(String[] args) throws IOException {
        scanner = new Scanner(System.in);
        for	(int i=0; i< namafile.length; i++)
        	System.out.println(i+1 + ". Penjadwalan " + namafile[i][1]);
        
        System.out.print("\nSilahkan pilih file untuk dijadwalkan : ");
        int pilih = scanner.nextInt();
        
        filePilihanInput = namafile[pilih-1][0];
        filePilihanOutput = namafile[pilih-1][1];
        
        file = folderDataset + filePilihanInput;
        
        course = new Course(file);
        jumlahexam = course.getJumlahCourse();
        
        conflict_matrix = new int[jumlahexam][jumlahexam];  
        conflict_matrix = course.getConflictMatrix();
        
        // print dataset array
		/*for (int i=0; i<10; i++) {
			for(int j=0; j<10; j++) {
				System.out.print(conflict_matrix[i][j] + " ");
			}
			System.out.println();
		}*/
		
		// sort exam by degree
		course_sorted = course.sortingByDegree(conflict_matrix, jumlahexam);
		//for (int i=0; i<jumlahexam; i++)
			//System.out.println("Degree of course " + course_sorted[i][0] + " is " + course_sorted[i][1]);
		
		schedule = new Schedule(filePilihanOutput, conflict_matrix, jumlahexam);
		timeslot = new int[jumlahexam];
		
		//int[][] conflict_matrix_copy = conflict_matrix;
		//int[][] graph = course_sorted;
		
		// start time
		long starttime = System.nanoTime();
		timeslot = schedule.schedulingByDegree(course_sorted, timeslot);
		long endtime = System.nanoTime();
		// end time
		double runningtime = (double) (endtime - starttime)/1000000000;
		
		int jumlahMurid = course.getJumlahMurid();
		
		schedule.printSchedule(filePilihanOutput, timeslot);
		
		System.out.println("Waktu eksekusi yang dibutuhkan adalah selama " + runningtime + " detik.");
		
		// write to sol file
		writeSolFile(hasil_timeslot, filePilihanOutput);
		System.out.println("Penalti : " + Evaluator.getPenalty(conflict_matrix, hasil_timeslot, jumlahMurid));
    }
    
    public static void writeSolFile(int[][] hasiltimeslot, String namaFileOutput) throws IOException {
		// fill hasiltimeslot array
    	hasil_timeslot = new int[jumlahexam][2];
    	for (int course = 0; course < jumlahexam; course++) {
    		hasil_timeslot[course][0] = (course+1);
    		hasil_timeslot[course][1] = timeslot[course];
    	}
    	
    	String directoryOutput = "C:/Users/ZAP/Google Drive/KULIAH/OKH/Tugas/UAS/ExamTimetableEvaluation/" + namaFileOutput +".sol";
        FileWriter writer = new FileWriter(directoryOutput, true);
        for (int i = 0; i <hasil_timeslot.length; i++) {
            for (int j = 0; j < hasil_timeslot[i].length; j++) {
                  writer.write(hasil_timeslot[i][j]+ " ");
            }
            writer.write("\n");
        }
        writer.close();
        
		System.out.println("\nFile penjadwalan " + namaFileOutput+ " berhasil dibuat");
	}
}