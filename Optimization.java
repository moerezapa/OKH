import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Optimization {
	static int[][] timeslotHillClimbing, conflict_matrix, course_sorted;
	int[] timeslot;
	String file;
	int jumlahexam;
	static int randomCourseIndex, randomTimeslot;
	/*Optimization(String file, int[][] conflict_matrix, int[][] course_sorted,int[] timeslot, int jumlahexam) {
		this.file = file;
		this.conflict_matrix = conflict_matrix;
		this.jumlahexam = jumlahexam;
		this.course_sorted = course_sorted;
		this.timeslot = timeslot;
	}*/
	
	// hill climbing method
	public static void getTimeslotByHillClimbing(String file, int iterasi) throws IOException {
		//int iterasi = 1000000; // iterasi 1 juta kali
		//int jumlah_timeslot = jumlahtimeslot;
		
		Course course = new Course(file);
        int jumlahexam = course.getJumlahCourse();
        
        int[][] conflict_matrix = new int[jumlahexam][jumlahexam];  
        conflict_matrix = course.getConflictMatrix();
        
        // print dataset array
		/*for (int i=0; i<10; i++) {
			for(int j=0; j<10; j++) {
				System.out.print(conflict_matrix[i][j] + " ");
			}
			System.out.println();
		}*/
		
		// sort exam by degree
		int[][] course_sorted = course.sortingByDegree(conflict_matrix, jumlahexam);
		//for (int i=0; i<jumlahexam; i++)
			//System.out.println("Degree of course " + course_sorted[i][0] + " is " + course_sorted[i][1]);
		
		Schedule schedule = new Schedule(file, conflict_matrix, jumlahexam);
		int[] timeslot = new int[jumlahexam];
		
		// start time
		long starttime = System.nanoTime();
		timeslot = schedule.schedulingByDegree(course_sorted, timeslot);
		long endtime = System.nanoTime();
		// end time
		double runningtime = (double) (endtime - starttime)/1000000000;
		
		int jumlahMurid = course.getJumlahMurid();
		
		schedule.printSchedule();
		
		int[][] jadwal = schedule.getSchedule();
		int[][] jadwaltemp = new int[jadwal.length][2];
		
		for (int i = 0; i < jadwaltemp.length; i ++) {
			jadwaltemp[i][0] = jadwal[i][0];
			jadwaltemp[i][1] = jadwal[i][1];
		}
		
		double penalti = Evaluator.getPenalty(conflict_matrix, jadwal, jumlahMurid);
		
		//System.out.println("Penalti : " + Evaluator.getPenalty(conflict_matrix, timeslot, jumlahMurid));
		
		for(int i = 0; i < iterasi; i++) {
//			System.out.println("iterasi "+(i+1));
//			for(int j = 0; j < jadwal.length; j++) {
//				System.out.println(jadwal[j][0]+ " " + jadwal[j][1]);
//			}
			try {
				randomCourseIndex = getRandomNumber(0, jumlahexam);
				randomTimeslot = getRandomNumber(0, schedule.getHowManyTimeSlot(timeslot));
//				System.out.println("random " + randomCourseIndex + " " + randomTimeslot);
				jadwaltemp[randomCourseIndex][1] = randomTimeslot;
			
//				System.out.println();
				
//				System.out.println(Scheduler.checkRandomTimeslot(randomCourseIndex, randomTimeslot, conflict_matrix, jadwal));
				if (Schedule.checkRandomTimeslot(randomCourseIndex, randomTimeslot, conflict_matrix, jadwal)) {	
					jadwaltemp[randomCourseIndex][1] = randomTimeslot;
					double penalti2 = Evaluator.getPenalty(conflict_matrix, jadwaltemp, jumlahMurid);
//					System.out.println("penalty = "+penalty+", penalty 2 = "+penalty2);
					if(penalti > penalti2) {
						penalti = Evaluator.getPenalty(conflict_matrix, jadwaltemp, jumlahMurid);
						jadwal[randomCourseIndex][1] = jadwaltemp[randomCourseIndex][1];
					} 
						else 
							jadwaltemp[randomCourseIndex][1] = jadwal[randomCourseIndex][1];
				}
//				System.out.println("\n###\n");
				//System.out.println("jadwaltemp ke " + randomCourseIndex);
				//System.out.println("Random timeslot ke " + randomTimeslot);
				System.out.println("Iterasi "+(i+1)+" - Penalty : "+penalti);
			}
				catch (ArrayIndexOutOfBoundsException e) {
					//System.out.println("randomCourseIndex index ke- " + randomCourseIndex);
					//System.out.println("randomTimeslot index ke- " + randomTimeslot);
				}
			
		}
		timeslotHillClimbing = new int[jumlahexam][2];
		timeslotHillClimbing = jadwal;
		System.out.println("\n================================================\n");
    	for (int i = 0; i < jumlahexam; i++)
    		System.out.println("Timeslot untuk course "+ timeslotHillClimbing[i][0] +" adalah timeslot: " + timeslotHillClimbing[i][1]);       
	    
    	System.out.println("\n================================================"); 
	    System.out.println("Jumlah timeslot yang dibutuhkan untuk menjadwalkan " + jumlahexam + " course di file " + file + " adalah "+ Arrays.stream(timeslot).max().getAsInt() + " timeslot.");
		System.out.println("Penalti akhir : " + penalti);
	}
	
	
	public static int getRandomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min) + min;
	}
	// another method
}
