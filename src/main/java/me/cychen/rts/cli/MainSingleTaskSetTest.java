package me.cychen.rts.cli;

import me.cychen.rts.event.BusyIntervalEventContainer;
import me.cychen.rts.event.EventContainer;
import me.cychen.rts.framework.Task;
import me.cychen.rts.framework.TaskSet;
import me.cychen.rts.scheduleak.DistributionMap;
import me.cychen.rts.scheduleak.ScheduLeakSporadic;
import me.cychen.rts.simulator.QuickFPSchedulerJobContainer;
import me.cychen.rts.simulator.QuickFixedPrioritySchedulerSimulator;
import me.cychen.rts.simulator.TaskSetContainer;
import me.cychen.rts.simulator.TaskSetGenerator;
import me.cychen.rts.util.ExcelLogHandler;
import me.cychen.util.Umath;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

/**
 * Created by cy on 3/28/2017.
 */
public class MainSingleTaskSetTest {
	static long SIM_DURATION = 100000;

	private static final int VICTIM_PRI = 2;
	private static final int OBSERVER_PRI = 1;

//	private static final Logger loggerConsole = LogManager.getLogger("console");
//	private static final Logger loggerExp2 = LogManager.getLogger("exp2");

	public static void main(String[] args) throws IOException {

        if(!(args.length == 2 || args.length == 4)){
            throw new IllegalArgumentException("\nprovided args of length: "+ args.length +"\nargs must be of length of 2 or 4"
            + "\narg[0] = taskset size\narg[1] = number of reps\narg[2](optional) "
            + "= utility upper limit\narg[3](optional) = utility lower limit");
        }
        int taskSetSize = Integer.parseInt(args[0]);
        int totalReps = Integer.parseInt(args[1]);


        float util_lower =-1;
        float util_upper =-1;
        if (args.length == 4){
            util_upper = Float.parseFloat(args[2]);
            util_lower = Float.parseFloat(args[3]);
        }


        for (int reps = 0; reps < totalReps; reps++) {
            String fileName = "size"+taskSetSize+"rep"+reps+"_meta.txt";

            try (BufferedWriter br = new BufferedWriter(new FileWriter(fileName))) {

//                loggerConsole.info("Test starts.");

                // Generate a task set.
                TaskSetGenerator taskSetGenerator = new TaskSetGenerator();

                // taskSetGenerator.setMaxPeriod(800);
                // taskSetGenerator.setMinPeriod(50);

                // taskSetGenerator.setMaxExecTime(50);
                // taskSetGenerator.setMinExecTime(5);

                taskSetGenerator.setMaxUtil(util_upper);
                taskSetGenerator.setMinUtil(util_lower);

                taskSetGenerator.setNonHarmonicOnly(true);

                // taskSetGenerator.setMaxHyperPeriod(70070);
                // taskSetGenerator.setGenerateFromHpDivisors(true);

                /* Optimal attack condition experiment. */
                taskSetGenerator.setNeedGenObserverTask(true);
                taskSetGenerator.setObserverTaskPriority(OBSERVER_PRI);
                taskSetGenerator.setVictimTaskPriority(VICTIM_PRI);

                taskSetGenerator.setMaxObservationRatio(999);
                taskSetGenerator.setMinObservationRatio(1);

                TaskSetContainer taskSets = taskSetGenerator.generate(taskSetSize, 1);

                TaskSet taskSet = taskSets.getTaskSets().get(0);

                // victim and observer task
                Task victimTask = taskSet.getOneTaskByPriority(VICTIM_PRI);
                Task observerTask = taskSet.getOneTaskByPriority(OBSERVER_PRI);

                double gcd = Umath.gcd(victimTask.getPeriod(), observerTask.getPeriod());
                double lcm = Umath.lcm(victimTask.getPeriod(), observerTask.getPeriod());
                double observationRatio = observerTask.getExecTime() / gcd;
                
                br.write(taskSet.toString());
                // loggerConsole.info(taskSet.toString());
                long taskSetHyperPeriod = taskSet.calHyperPeriod();
//                loggerConsole.info("Task Hyper-period: " + taskSetHyperPeriod);
            }
        }
	}
}
