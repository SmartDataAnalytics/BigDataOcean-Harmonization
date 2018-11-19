package org.unibonn.bdo.connections;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.*;

/**
 * 
 * Class for the HDFSFileSystems
 *
 */

public class HDFSFileSystem {
	
	private static final Logger log = LoggerFactory.getLogger(HDFSFileSystem.class);

    private FileSystem fileSystem;

    public HDFSFileSystem(String hdfsURI) {
    	
    	try {
            // Init HDFS file system object
            Configuration conf = new Configuration();

            // Set FileSystem URI
            conf.set("fs.defaultFS", hdfsURI);
            conf.setBoolean("dfs.support.append", true);

            // Because of Maven
            conf.set("fs.AbstractFileSystem.file.impl", org.apache.hadoop.fs.local.LocalFs.class.getName());
            conf.set("fs.AbstractFileSystem.hdfs.impl", org.apache.hadoop.fs.Hdfs.class.getName());

            // Set HADOOP user
            System.setProperty("HADOOP_USER_NAME", "hdfs");
            System.setProperty("hadoop.home.dir", "/");

            // Get the filesystem - HDFS
            this.fileSystem = FileSystem.get(conf);
        } catch (IOException e) {
        	log.error(e.getMessage());
        }
    }

    public BufferedReader readFile(String path) {
        FSDataInputStream inputStream = null;

        try {
            Path hdfsreadpath = new Path(path);
            // Init input stream
            inputStream = fileSystem.open(hdfsreadpath);
            
            return new BufferedReader(new InputStreamReader(inputStream.getWrappedStream()));

        } catch (Exception e) {
        	log.error(e.getMessage());
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                	log.error(e.getMessage());
                }
            }
        }

        return null;
    }

    public byte[] readFileToByteArray(String path) {
        FSDataInputStream inputStream = null;

        try {
            Path hdfsreadpath = new Path(path);
            // Init input stream
            inputStream = fileSystem.open(hdfsreadpath);

            return IOUtils.toByteArray(inputStream);
        } catch (Exception e) {
        	log.error(e.getMessage());
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                	log.error(e1.getMessage());
                }
            }
        }

        return null;
    }
    
    public Path copyFile(String path, String local) {
    	Path hdfsFilePath = new Path(path);
    	Path localPath = new Path(local);
    	try {
			fileSystem.copyToLocalFile(hdfsFilePath, localPath);
			fileSystem.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
    	
    	return localPath;
    }
    
    public void deleteFile(String path) {
    	Path hdfsFilePath = new Path(path);
    	try {
    		if (fileSystem.exists(hdfsFilePath)) {
				fileSystem.delete(hdfsFilePath, true);
				fileSystem.close();
    		}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
    }
}

