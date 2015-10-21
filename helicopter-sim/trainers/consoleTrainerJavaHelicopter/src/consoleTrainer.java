/* console Trainer for RL Competition
* Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */
import org.rlcommunity.rlglue.codec.RLGlue;
import java.io.FileWriter;
import java.io.IOException;

public class consoleTrainer {


	public static void main(String[] args) throws InterruptedException {
	
		int whichTrainingMDP = 1; // select the MDP to load

		consoleTrainerHelper.loadHelicopter(whichTrainingMDP);	
		RLGlue.RL_init();

        int evaluationCount = 100; //number of evaluatins to perfom
		int episodeCount=100; //number of episodes to run between evaluations
                                //total number of episodes = evaluationCount * episodeCount
		int maxEpisodeLength=20000; //set a maxEpisodeLength to cut off long episodes
	
		int totalSteps=0;//counter for the total number of steps taken to finish all episodes
		//run the episodes with RL_episode(maxEpisodeLength)
        
        saveScore(true, 0, null);
        
        boolean firstTime = true;
        long startTime = 0;
        long endTime = 0;
        double timeTaken = 0;
        
        for(int j=0;j<evaluationCount;j++){
            System.out.println("Starting Episode Iteration " + j);
            for(int i=0;i<episodeCount;i++){
                if(firstTime){
                    startTime = System.currentTimeMillis();
                }
                RLGlue.RL_episode(maxEpisodeLength);
                if(firstTime){
                    endTime = System.currentTimeMillis();
                    timeTaken = endTime - startTime;
                    timeTaken /= 1000.0; //seconds
                    System.out.println("Eposode Time: " + timeTaken + " seconds");
                    
                    timeTaken *= evaluationCount * episodeCount;
                    System.out.println("Experiment Time: " + (timeTaken / 60.0) + " minutes");
                    
                    firstTime = false;
                }
                totalSteps+=RLGlue.RL_num_steps();
                //System.out.println("Episode: "+i+" steps: "+RLGlue.RL_num_steps());
            }
            evaluationPoint evp = evaluateAgent(maxEpisodeLength, 50);
            printScore(j*episodeCount, evp);
            saveScore(false, j*episodeCount, evp);
        }
		
	
		System.out.println("totalSteps is: "+totalSteps);
		
		//clean up the environment and end the program
		RLGlue.RL_cleanup();
	}
    
    static class evaluationPoint {
        
        public double mean;
        public double standardDeviation;
        public int minSteps;
        public int maxSteps;
        public double averageSteps;
        
        
        public evaluationPoint(double mean, double standardDeviation, int minSteps, int maxSteps, double averageSteps) {
            this.mean = mean;
            this.standardDeviation = standardDeviation;
            this.minSteps = minSteps;
            this.maxSteps = maxSteps;
            this.averageSteps = averageSteps;
        }
    }
    
    /**
     * Tell the agent to stop learning, then execute n episodes with his current
     * policy.  Estimate the mean and variance of the return over these episodes.
     * @return
     */
    static evaluationPoint evaluateAgent(int maxEpisodeLength, int episodesToEvaluate) {
        double sum = 0;
        double sum_of_squares = 0;
        double this_return = 0;
        double mean;
        double variance;
        
        int maxSteps = 0;
        int minSteps = Integer.MAX_VALUE;
        double sumSteps = 0;
        
        RLGlue.RL_agent_message("freeze-learning");
        for (int i = 0; i < episodesToEvaluate; i++) {
            /* We use a cutoff here in case the policy is bad
             and will never end an episode */
            RLGlue.RL_episode(maxEpisodeLength);
            this_return = RLGlue.RL_return();
            sum += this_return;
            sum_of_squares += this_return * this_return;
            
            int steps = RLGlue.RL_num_steps();
            if(steps > maxSteps){
                maxSteps = steps;
            }else if(steps < minSteps){
                minSteps = steps;
            }
            sumSteps += steps;
        }
        RLGlue.RL_agent_message("unfreeze-learning");
        
        mean = sum / (double)episodesToEvaluate;
        variance = (sum_of_squares - (double)episodesToEvaluate * mean * mean) / ((double)episodesToEvaluate - 1.0f);
        return new evaluationPoint(mean, Math.sqrt(variance), minSteps, maxSteps, sumSteps/(double)episodesToEvaluate);
    }
    /*
     This function will freeze the agent's policy and test it after every 25 episodes.
     */
    static void printScore(int afterEpisodes, evaluationPoint theScore) {
        System.out.printf("Episodes: %d\tMean: %.2f\tSTD: %.2f\tSteps: %.2f\n", afterEpisodes, theScore.mean, theScore.standardDeviation, theScore.averageSteps);
    }
    
    static void saveScore(boolean initial, int afterEpisodes, evaluationPoint theScore){
        try{
            FileWriter writer = new FileWriter("experiementResults.csv", true);
            if(initial){
                writer.append("Episodes");
                writer.append(',');
                writer.append("Mean");
                writer.append(',');
                writer.append("STD");
                writer.append(',');
                writer.append("MinSteps");
                writer.append(',');
                writer.append("MaxSteps");
                writer.append(',');
                writer.append("AvgSteps");
                writer.append('\n');
            }else{
                writer.append("" + afterEpisodes);
                writer.append(',');
                writer.append("" + theScore.mean);
                writer.append(',');
                writer.append("" + theScore.standardDeviation);
                writer.append(',');
                writer.append("" + theScore.minSteps);
                writer.append(',');
                writer.append("" + theScore.maxSteps);
                writer.append(',');
                writer.append("" + theScore.averageSteps);
                writer.append('\n');
            }
        
            //generate whatever data you want
        
            writer.flush();
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
