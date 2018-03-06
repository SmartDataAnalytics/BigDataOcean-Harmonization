package org.unibonn.bdo.bdodatasets;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HDFSFileSystem {
    private final static Logger logger = Logger.getLogger(HDFSFileSystem.class.getName());

    private FileSystem fileSystem;

    public HDFSFileSystem(String hdfsURI) {

        try {
            Configuration conf = new Configuration();

            // Set FileSystem URI
            conf.set("fs.defaultFS", hdfsURI);
            conf.set("fs.default.name", hdfsURI);

            // Because of Maven
            conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
            conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

            // Set HADOOP user
            System.setProperty("HADOOP_USER_NAME", "ubuntu");

            FileSystem fs = FileSystem.get(URI.create(hdfsURI), conf);

            this.fileSystem = fs;

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public BufferedReader readFile(String path) {
        FSDataInputStream inputStream = null;

        try {

            logger.info("Read file from hdfs path: " + path);
            Path hdfsreadpath = new Path(path);
            // Init input stream
            inputStream = fileSystem.open(hdfsreadpath);
            
            return new BufferedReader(new InputStreamReader(inputStream));

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }

        return null;
    }

    public byte[] readFileToByteArray(String path) {
        FSDataInputStream inputStream = null;

        try {
            logger.info("Read file from hdfs path: " + path);
            Path hdfsreadpath = new Path(path);
            // Init input stream
            inputStream = fileSystem.open(hdfsreadpath);

            return IOUtils.toByteArray(inputStream);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return localPath;
    }
}

