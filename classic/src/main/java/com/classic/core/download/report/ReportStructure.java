package com.classic.core.download.report;

import com.classic.core.download.Utils.helper.FileUtils;
import com.classic.core.download.core.enums.TaskStates;
import com.classic.core.download.database.elements.Chunk;
import com.classic.core.download.database.elements.Task;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Majid Golshadi on 4/10/2014.
 */
public class ReportStructure {

    private int id;
    private String name;
    private int state;
    private String url;
    private long fileSize;
    private boolean resumable;
    private String type;
    private int chunks;
    private double percent;
    private long downloadLength;
    private String saveAddress;
    private boolean priority;

    public long setDownloadLength(long downloadedLength){
        return downloadLength += downloadedLength;
    }

    public long getTotalSize(){
        return fileSize;
    }

    public boolean isResumable(){
        return resumable;
    }

    public ReportStructure setObjectValues(Task task, List<Chunk> taskChunks){
        this.id = task.id;
        this.name = task.name;
        this.state = task.state;
        this.resumable = task.resumable;
        this.url = task.url;
        this.fileSize = task.size;
        this.type = task.extension;
        this.chunks = task.chunks;
        this.priority = task.priority;
        this.saveAddress = task.save_address+"/"+task.name+"."+task.extension;

        this.percent = calculatePercent(task, taskChunks);

        return this;
    }

    /** calculate download percent from compare chunks size with real file size **/
    private double calculatePercent(Task task, List<Chunk> chunks){
    	// initialize report
    	double report = 0;
    	
    	// if download not completed we have chunks 
    	if (task.state != TaskStates.DOWNLOAD_FINISHED) {
	        int sum = 0;    
	        for (Chunk chunk : chunks){
	            this.downloadLength += FileUtils.size(task.save_address, String.valueOf(chunk.id));
	        }
	
	        if (task.size > 0) {
	            report = ((float)downloadLength / task.size * 100);
	        }   
    	} else {
    		this.downloadLength = task.size;
    		report = 100;
    	}
    	
    	return report;
    }


    public JSONObject toJsonObject(){
        JSONObject json = new JSONObject();
        try {
            return json.put("token", String.valueOf(id))
                    .put("name", name)
                    .put("state", state)
                    .put("resumable", resumable)
                    .put("fileSize", fileSize)
                    .put("url", url)
                    .put("type", type)
                    .put("chunks", chunks)
                    .put("percent", percent)
                    .put("downloadLength", downloadLength)
                    .put("saveAddress", saveAddress)
                    .put("priority", priority);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
