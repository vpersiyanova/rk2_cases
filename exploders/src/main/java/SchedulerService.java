import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Solovyev on 09/04/2017.
 */
public class SchedulerService {

    private AtomicLong serverTime = new AtomicLong(0);

    private SortedMap<Long, Job> awaitingJobs = new TreeMap<>();

    public void gmTick(long frameMillis) {
        final long currentTime = serverTime.addAndGet(frameMillis);
        final SortedMap<Long, Job> jobsToExecute = awaitingJobs.headMap(currentTime);
        for (Map.Entry<Long, Job> longJobEntry : jobsToExecute.entrySet()) {
            try {
                longJobEntry.getValue().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        awaitingJobs = awaitingJobs.tailMap(currentTime);
        System.out.println("tick-tock. Time is " + serverTime);
    }

    public void submit(Job job, long timestamp) {
        awaitingJobs.put(timestamp, job);
    }

    public long getServerTime() {
        return serverTime.get();
    }
}
