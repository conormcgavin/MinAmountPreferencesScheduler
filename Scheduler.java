package finalyearproject;

import java.util.ArrayList;
import org.chocosolver.util.tools.ArrayUtils;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;


public class Scheduler {
	Model model;
	
	int[] workers_needed_per_day = {1, 1, 1, 1, 1, 1, 1};
	
	int num_workers;
	
	IntVar[][] timetable;
	
	ArrayList<IntVar[]> preferences;
	
	IntVar[] total_preferences;

	ArrayList<Employee> employees;
	
	public Scheduler(ArrayList<Employee> employees) {
		Model model = new Model("FYP");
		this.model = model;
		this.num_workers = employees.size();
		this.employees = employees;
		
		this.timetable = model.intVarMatrix("Timetable", num_workers, Constants.days_per_week, 0, 1);
		
		this.preferences = new ArrayList<IntVar[]>();
	
		for (int i=0; i<num_workers; i++) {
			preferences.add(model.intVarArray("PreferenceAssignments", Constants.PREFERENCES_PER_PERSON, 0, 1));
		}
		
		this.total_preferences = model.intVarArray("TotalPreferences", this.num_workers, 0, Constants.PREFERENCES_PER_PERSON);
		for (int i = 0; i < this.num_workers; i++) {
			model.sum(preferences.get(i), "=", total_preferences[i]).post();
		}
		
		this.addHardConstraints();
	}

	private void addHardConstraints() {
		// the total of the entire matrix should add up to the sum of workers_hours_per_week
		int sum_workers_needed = 0;
	    for (int value : workers_needed_per_day) {
	        sum_workers_needed += value;
	    }
		model.sum(ArrayUtils.flatten(timetable), "=", sum_workers_needed).post();
		
		// every column should have a sum of exactly the sum of workers needed in that day
		for (int i=0; i<Constants.days_per_week; i++) {
			model.sum(ArrayUtils.getColumn(timetable, i), "=", workers_needed_per_day[i]).post();
		}
		
		// workers must work between their working day limits every week
		for (int i=0; i<num_workers; i++) {
			model.sum(timetable[i], "<=", employees.get(i).max_days_per_week).post();
			model.sum(timetable[i], ">=", employees.get(i).min_days_per_week).post();
		}
	}
	
	public void addPreference(int person, Preference p) {
		if (p.modelAsHard == false) {
			model.ifOnlyIf(this.model.arithm(timetable[person][p.day], "=", 0), this.model.arithm(preferences.get(person)[p.order], "=", 1));
		} else {
			model.arithm(timetable[person][p.day], "=", 0).post();
			model.arithm(preferences.get(person)[p.order-1], "=", 1).post();
		}
		
	}
	
	public void optimise() {
		model.allEqual(total_preferences).post();
	}
	
	public Solution solve() {
		System.out.println("Solving...");
		Solver solver = model.getSolver();
		model.setObjective(Model.MAXIMIZE, total_preferences[0]);
		Solution solution = new Solution(model);
		while (solver.solve()) {
			solution.record();
			printSolution();
			return solution;
		}	
		
		return solution;
	}
	
	public void printSolution() {
		System.out.println("Printing Optimal Solution...\n");
		System.out.println("-----------------------------------------------------------------------------");
		String row_sol;
		for (int i = 0; i < num_workers; i++) {
			int count = 0;
			row_sol = "Worker " + i + ":\t";
			for (int j = 0; j < Constants.days_per_week; j++) {
				row_sol += timetable[i][j].getValue() + "\t";
				count += 1;
				if (count == Constants.hours_per_day) {
					row_sol += " | \t";
					count = 0;
				}
			}
			System.out.println(row_sol);
			
		}	
		System.out.println("-----------------------------------------------------------------------------\n");
	}
	
	public void printInformation() {
		System.out.println("\n********************************************************************************");
		System.out.println("Printing information from this weekly run...");
		System.out.println("-----------------------------------");
		System.out.println("Printing preference assignments...");
		for (int i = 0; i < num_workers; i++) {
			System.out.println("Worker " + i + ": ");
			for (int j = 0; j < Constants.PREFERENCES_PER_PERSON; j++) {
				System.out.print(preferences.get(i)[j].getValue() + "\t");
			}
			System.out.println();
		}
		System.out.println("-----------------------------------");
		
	
		System.out.println("-----------------------------------");
		System.out.println("Printing preferences per person...");
		System.out.println(total_preferences[0].getValue());
		System.out.println("-----------------------------------");
		System.out.println("********************************************************************************\n");
	}

}






