package finalyearproject;
import java.util.ArrayList;

public class Employee {
	
	ArrayList<Integer> pref_score_history;
	
	int[] preferences_received;
	
	ArrayList<Preference> preferences;
	ArrayList<Preference> preference_history;
	
	String name;
	int min_days_per_week;
	int max_days_per_week;
	int skill_level;
	
	public Employee(String name, int min_days, int max_days, int skill_level) {
		this.preferences = new ArrayList<Preference>();
		this.preferences_received = new int[Constants.PREFERENCES_PER_PERSON];
		
		this.preference_history = new ArrayList<Preference>();
		this.pref_score_history = new ArrayList<Integer>();
		
		this.name = name;
		this.min_days_per_week = min_days;
		this.max_days_per_week = max_days;
		this.skill_level = skill_level;
	}

	
	public void addPreference(int week, int day, int order) {
		Preference r = new Preference(week, day, order, false); 
		preferences.add(r);
		
	}
	

	public ArrayList<Preference> getPreferences() {
		return this.preferences;
		
	}
	
	
	public void printStats() {
		System.out.println("********************************************************************************");
		System.out.println("Printing stats for " + name);
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("Printing Preference stats...");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("Preferences received: ");
		for (int i=0; i<Constants.PREFERENCES_PER_PERSON; i++) {
			System.out.println(i + ": " + preferences_received[i]);
		}
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("Preference history: ");
		for (Preference preference : preference_history) {
			System.out.println("Week: " + preference.week + ", Order: " + preference.order + ", Granted: " + preference.granted);
		}
		System.out.println("********************************************************************************\n");
	}
	
}