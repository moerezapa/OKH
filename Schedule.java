import java.util.Arrays;

public class Schedule {
	
	String file;
	int[][] conflictmatrix, timeslotSchedule;
	int[] timeslot;
	int jumlahexam;
	int timeslotindex;
	int timeslotmax;
	
	public Schedule(String file, int[][] conflictmatrix, int jumlahexam) {
		this.file = file;
		this.conflictmatrix = conflictmatrix;
		this.jumlahexam = jumlahexam;
	}
	
	public int[][] getSchedule() {
		// fill hasiltimeslot array
		timeslotSchedule = new int[jumlahexam][2];
    	for (int course = 0; course < jumlahexam; course++) {
    		timeslotSchedule[course][0] = (course+1);
    		timeslotSchedule[course][1] = timeslot[course];
    	}
		return this.timeslotSchedule; 
	}
	
	public int[] scheduling(int[] timeslot) {
		timeslot = new int[jumlahexam];
		timeslotindex = 1;
    	for(int i= 0; i < conflictmatrix.length; i++)
    		timeslot[i] = 0;
    	
		for(int i = 0; i < conflictmatrix.length; i++) {
			for (int j = 1; j <= timeslotindex; j++) {
				if(isTimeslotAvailable(i, j, conflictmatrix, timeslot)) {
					timeslot[i] = j;
					break;
				}
					else
						timeslotindex = timeslotindex+1;
			}
		}
		return this.timeslot;
	}
	public int[] schedulingByDegree(int [][] sortedCourse, int[] timeslot) {
    	this.timeslot = new int[jumlahexam];
    	timeslotindex = 1; // starting timeslot from 1
    	for(int i= 0; i < sortedCourse.length; i++)
    		this.timeslot[i] = 0;
    	
		for(int course = 0; course < sortedCourse.length; course++) {
			for (int time_slotindex = 1; time_slotindex <= timeslotindex; time_slotindex++) {
				if(isTimeslotAvailableWithSorted(course, time_slotindex, conflictmatrix, sortedCourse, this.timeslot)) {
					this.timeslot[sortedCourse[course][0]-1] = time_slotindex;
					break;
				}
					else
						timeslotindex = timeslotindex+1; // move to ts+1 if ts is crash
			}
		}
		return this.timeslot;
    }
	
	public int[] schedulingBySaturationDegree(int [][] sortedCourse, int[] timeslot) {
    	this.timeslot = new int[jumlahexam];
    	timeslotindex = 1; // starting timeslot from 1
    	for(int i= 0; i < sortedCourse.length; i++)
    		this.timeslot[i] = 0;
    	
		for(int course = 0; course < sortedCourse.length; course++) {
			for (int time_slotindex = 1; time_slotindex <= timeslotindex; time_slotindex++) {
				if(isTimeslotAvailableWithSaturation(course, time_slotindex, conflictmatrix, sortedCourse, this.timeslot)) {
					this.timeslot[sortedCourse[course][0]-1] = time_slotindex;
					break;
				}
					else
						timeslotindex = timeslotindex+1; // move to ts+1 if ts is crash
			}
		}
		return this.timeslot;
    }
	
	public int getHowManyTimeSlot(int[] timeslot) { return Arrays.stream(timeslot).max().getAsInt(); }
	
	public static boolean isTimeslotAvailable(int course, int timeslot, int[][] conflictmatrix, int[] timeslotarray) {
		for(int i = 0; i < conflictmatrix.length; i++)
			if(conflictmatrix[course][i] != 0 && timeslotarray[i] == timeslot)
				return false;
		
		return true;
	}
    public static boolean isTimeslotAvailableWithSorted(int course, int timeslot, int[][] conflictmatrix, int[][] sortedmatrix, int[] timeslotarray) {
		for(int i = 0; i < sortedmatrix.length; i++) 
			if(conflictmatrix[sortedmatrix[course][0]-1][i] != 0 && timeslotarray[i] == timeslot) {
				return false;
			}
		
		return true;
	}
    public static boolean isTimeslotAvailableWithSaturation(int course, int timeslot, int[][] conflictmatrix, int[][] sortedmatrix, int[] timeslotarray) {
		for(int i = 0; i < sortedmatrix.length; i++) 
			if(conflictmatrix[sortedmatrix[course][0]-1][i] != 0 && timeslotarray[i] == timeslot) {
				return false;
			}
		
		return true;
	}
    
    public static boolean checkRandomTimeslot(int randomCourse, int randomTimeslot, int[][] conflict_matrix, int[][] jadwal){
        for(int i=0; i<conflict_matrix.length; i++)
            if(conflict_matrix[randomCourse][i] !=0 && jadwal[i][1]==randomTimeslot)
                return false;
        return true;              
    }
    
    public static boolean checkRandomTimeslotForSA(int courseSwap1, int courseSwap2, int timeslotpos1, int timeslotpos2, int[][] conflict_matrix, int[][] jadwal){
        for(int i=0; i<conflict_matrix.length; i++)
            if((conflict_matrix[courseSwap2][i] !=0 && jadwal[i][1]==timeslotpos1) && (conflict_matrix[courseSwap1][i] !=0 && jadwal[i][1]==timeslotpos2))
                return false;
        return true;              
    }
    
	public void printSchedule() {
		System.out.println("\n================================================\n");
    	for (int i = 0; i < jumlahexam; i++)
    		System.out.println("Timeslot untuk course "+ (i+1) +" adalah timeslot: " + timeslot[i]);       
	    
    	System.out.println("\n================================================"); 
	    System.out.println("Jumlah timeslot yang dibutuhkan untuk menjadwalkan " + jumlahexam + " course di file " + file + " adalah "+ Arrays.stream(timeslot).max().getAsInt() + " timeslot.");
	}
}
