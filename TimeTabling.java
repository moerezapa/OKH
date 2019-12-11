import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TimeTabling {

    static String folderDataset = "C:/Users/ZAP/Google Drive/KULIAH/OKH/Tugas/UAS/Dataset/Toronto/";
    static String namafile[][] = {	{"car-f-92", "Carleton92"}, {"car-s-91", "Carleton91"}, {"ear-f-83", "EarlHaig83"}, {"hec-s-92", "EdHEC92"}, 
									{"kfu-s-93", "KingFahd93"}, {"lse-f-91", "LSE91"}, {"pur-s-93", "pur93"}, {"rye-s-93", "rye92"}, {"sta-f-83", "St.Andrews83"},
									{"tre-s-92", "Trent92"}, {"uta-s-92", "TorontoAS92"}, {"ute-s-92", "TorontoE92"}, {"yor-f-83", "YorkMills83"}
								};
    
    static int timeslot[]; // fill with course & its timeslot
    static int[][] conflict_matrix, course_sorted, hasil_timeslot;
	
	private static Scanner scanner;
	
    public static void main(String[] args) throws IOException {
        scanner = new Scanner(System.in);
        for	(int i=0; i< namafile.length; i++)
        	System.out.println(i+1 + ". Penjadwalan " + namafile[i][1]);
        
        System.out.print("\nSilahkan pilih file untuk dijadwalkan : ");
        int pilih = scanner.nextInt();
        
        String filePilihanInput = namafile[pilih-1][0];
        String filePilihanOutput = namafile[pilih-1][1];
        
        String file = folderDataset + filePilihanInput;
        
        /*
         * Scheduling from largest degree (default timesloting)
         */
        /*course = new Course(file);
        jumlahexam = course.getJumlahCourse();
        
        conflict_matrix = new int[jumlahexam][jumlahexam];  
        conflict_matrix = course.getConflictMatrix();
        
        // print dataset array
		/*for (int i=0; i<10; i++) {
			for(int j=0; j<10; j++) {
				System.out.print(conflict_matrix[i][j] + " ");
			}
			System.out.println();
		}
		
		// sort exam by degree
		course_sorted = course.sortingByDegree(conflict_matrix, jumlahexam);
		//for (int i=0; i<jumlahexam; i++)
			//System.out.println("Degree of course " + course_sorted[i][0] + " is " + course_sorted[i][1]);
		
		schedule = new Schedule(filePilihanOutput, conflict_matrix, jumlahexam);
		timeslot = new int[jumlahexam];
		
		
		// start time
		long starttime = System.nanoTime();
		timeslot = schedule.schedulingByDegree(course_sorted, timeslot);
		long endtime = System.nanoTime();
		// end time
		double runningtime = (double) (endtime - starttime)/1000000000;
		
		int jumlahMurid = course.getJumlahMurid();
		
		schedule.printSchedule(timeslot);
		
		System.out.println("Waktu eksekusi yang dibutuhkan adalah selama " + runningtime + " detik.");
		
		// write to sol file
		writeSolFile(hasil_timeslot, filePilihanOutput);
		System.out.println("Penalti : " + Evaluator.getPenalty(conflict_matrix, hasil_timeslot, jumlahMurid));
		*/
		
        Course course = new Course(file);
        int jumlahexam = course.getJumlahCourse();
        
        conflict_matrix = course.getConflictMatrix();
        int jumlahmurid = course.getJumlahMurid();
        
		// sort exam by degree
		course_sorted = course.sortingByDegree(conflict_matrix, jumlahexam);
		
		/*
		 * params 1: file to be scheduling
		 * params 2: conflict matrix from file
		 * params 3: sort course by degree
		 * params 4: how many course from file
		 * params 5: how many student from file
		 * params 6: how many iterations
		 */
		Optimization optimization = new Optimization(file, conflict_matrix, course_sorted, jumlahexam, jumlahmurid, 1000);
		/*
		 * use hill climbing for timesloting
		 */
		long starttimeHC = System.nanoTime();
		optimization.getTimeslotByHillClimbing1(); // use hillclimbing methode for iterates 1000000 times
		long endtimeHC = System.nanoTime();
		
		/*
		 * use simmulated annealing for timesloting
		 * params : temperature
		 */
		long starttimeSA = System.nanoTime();
		optimization.getTimeslotBySimulatedAnnealing(100.0);
		long endtimeSA = System.nanoTime();
		/*
		 * use tabu search for timeslotting
		 */
		long starttimeTS = System.nanoTime();
		optimization.getTimeslotByTabuSearch();
		long endtimeTS = System.nanoTime();
		// end time
		System.out.println("Timeslot dibutuhkan (menggunakan Hill Climbing) 		: " + optimization.getJumlahTimeslotHC());
		System.out.println("Penalti Hill Climbing 						: " + Evaluator.getPenalty(conflict_matrix, optimization.getTimeslotHillClimbing(), jumlahmurid));
		System.out.println("Waktu eksekusi yang dibutuhkan Hill Climbing " + ((double) (endtimeHC - starttimeHC)/1000000000) + " detik.\n");
		
		System.out.println("Timeslot dibutuhkan (menggunakan Simulated Annealing) 		: " + optimization.getJumlahTimeslotSimulatedAnnealing());
		System.out.println("Penalti Simulated Annealing 					: " + Evaluator.getPenalty(conflict_matrix, optimization.getTimeslotSimulatedAnnealing(), jumlahmurid));
		System.out.println("Waktu eksekusi yang dibutuhkan Simmulated Annealing " + ((double) (endtimeSA - starttimeSA)/1000000000) + " detik.\n");
		
		System.out.println("Timeslot dibutuhkan (menggunakan Tabu Search) 			: " + optimization.getJumlahTimeslotTabuSearch());
		System.out.println("Penalti Tabu Search 						: " + Evaluator.getPenalty(conflict_matrix, optimization.getTimeslotTabuSearch(), jumlahmurid));
		System.out.println("Waktu eksekusi yang dibutuhkan Tabu Search " + ((double) (endtimeTS - starttimeTS)/1000000000) + " detik.");
		
//		double[] penaltyList = optimization.getTabuSearchPenaltyList();
//		hasil_timeslot = optimization.getTimeslotHillClimbing();
//		hasil_timeslot = optimization.getTimeslotSimulatedAnnealing();
//		writePenaltyListFile(penaltyList, filePilihanOutput);
    }
    
    public static void writeSolFile(int[][] hasiltimeslot, String namaFileOutput) throws IOException {
    	String directoryOutput = "C:/Users/ZAP/Google Drive/KULIAH/OKH/Tugas/UAS/ExamTimetableEvaluation/" + namaFileOutput +".sol";
        FileWriter writer = new FileWriter(directoryOutput, true);
        for (int i = 0; i < hasiltimeslot.length; i++) {
            for (int j = 0; j < hasiltimeslot[i].length; j++) {
                  writer.write(hasiltimeslot[i][j]+ " ");
            }
            writer.write("\n");
        }
        writer.close();
        
		System.out.println("\nFile penjadwalan " + namaFileOutput+ " berhasil dibuat");
	}
    
    public static void writePenaltyListFile(double[] penaltyList, String namaFileOutput) throws IOException {
    	String directoryOutput = "C:/Users/ZAP/Google Drive/KULIAH/OKH/Tugas/UAS/" + namaFileOutput +".txt";
        FileWriter writer = new FileWriter(directoryOutput, true);
        
        for (int j = 0; j < penaltyList.length; j++) {
            writer.write(penaltyList[j]+ " ");
            writer.write("\n");
//        	System.out.println(penaltyList[j]);
        }
        writer.close();
        
		System.out.println("\nFile list penalty " + namaFileOutput+ " berhasil dibuat");
	}
}