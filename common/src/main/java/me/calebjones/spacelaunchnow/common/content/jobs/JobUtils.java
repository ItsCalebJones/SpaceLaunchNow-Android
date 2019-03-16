package me.calebjones.spacelaunchnow.common.content.jobs;


import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.Date;
import java.util.Set;

import me.calebjones.spacelaunchnow.common.utils.Utils;
import timber.log.Timber;

public class JobUtils {

    public static Set<JobRequest> getJobRequests(){
        Set<JobRequest> jobRequests = JobManager.instance().getAllJobRequests();
        return jobRequests;
    }

    public static void logJobRequest(){
        String content = "";
        Set<JobRequest> jobRequests = getJobRequests();
        for (JobRequest jobRequest : jobRequests) {

            String time;
            Date date = new Date();
            if (!jobRequest.isPeriodic()) {
                if (jobRequest.getStartMs() == jobRequest.getEndMs()) {
                    date = new Date(date.getTime() + jobRequest.getStartMs());
                    time = "for " + Utils.getFormattedDateFromTimestamp(date.getTime());
                } else {
                    Date sDate = new Date(date.getTime() + jobRequest.getStartMs());
                    Date eDate = new Date(date.getTime() + jobRequest.getEndMs());
                    time = "between " + Utils.getFormattedDateFromTimestamp(sDate.getTime()) + " and " + Utils.getFormattedDateFromTimestamp(eDate.getTime());
                }
            } else {
                date = new Date(jobRequest.getScheduledAt() + jobRequest.getIntervalMs());
                time = "around " + Utils.getFormattedDateFromTimestamp(date.getTime());
            }
            String message = String.format("%s scheduled %s \n", jobRequest.getTag(), time);
            Timber.i(message);
        }
    }

    public static String getJobRequestStatus(){
        String content = "";
        Set<JobRequest> jobRequests = getJobRequests();
        for (JobRequest jobRequest : jobRequests) {

            String time;
            Date date = new Date();
            if (!jobRequest.isPeriodic()) {
                if (jobRequest.getStartMs() == jobRequest.getEndMs()) {
                    date = new Date(date.getTime() + jobRequest.getStartMs());
                    time = "for " + Utils.getFormattedDateFromTimestamp(date.getTime());
                } else {
                    Date sDate = new Date(date.getTime() + jobRequest.getStartMs());
                    Date eDate = new Date(date.getTime() + jobRequest.getEndMs());
                    time = "between " + Utils.getFormattedDateFromTimestamp(sDate.getTime()) + " and " + Utils.getFormattedDateFromTimestamp(eDate.getTime());
                }
            } else {
                date = new Date(jobRequest.getScheduledAt() + jobRequest.getIntervalMs());
                time = "around " + Utils.getFormattedDateFromTimestamp(date.getTime());
            }
            String message = String.format("%s scheduled %s \n", jobRequest.getTag(), time);
            Timber.d(message);
            content = content + message;
        }
        return content;
    }
}
