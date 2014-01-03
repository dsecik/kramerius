package org.gradle

import org.gradle.api.file.*;
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.artifacts.*;

import org.apache.tools.ant.taskdefs.optional.ANTLR;
import org.apache.tools.ant.types.Path;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.antlr.internal.GenerationPlan;
import org.gradle.api.plugins.antlr.internal.GenerationPlanBuilder;
import org.gradle.api.plugins.antlr.internal.MetadataExtracter;
import org.gradle.api.plugins.antlr.internal.XRef;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.util.GFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import com.sun.tools.xjc.*

/** XJC Task; call as java code */
public class XJCTask extends SourceTask {
	
	/** Default package name */
	public static final String DEFAULT_PACKAGE_NAME="com.qbizm.kramerius.imp.jaxb";

	private static final Logger LOGGER = LoggerFactory.getLogger(XJCTask.class);
	
    
	private FileCollection xjcClasspath;
	private File outputDirectory;
	private String packageName = DEFAULT_PACKAGE_NAME;
	
	
	
	public String getPackageName() {
		return this.packageName;
	}
	
	public void setPackageName(String pckg) {
		this.packageName = pckg;
	}
    
	
	public void setXjcClasspath(FileCollection p) {
		println "setting classpath $p"
		this.xjcClasspath = p;
    	}
    
	@OutputDirectory
	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

    	
	@InputFiles
	public FileCollection getXjcClasspath() {
		return this.xjcClasspath;
	}
    	
	private void configureExt(JAXBExtensions ext) {
		setPackageName(ext.getPackageName());
	}
    
	@TaskAction
	public void generate() {
		
    	    JAXBExtensions ext = getProject().getExtensions().getByType(JAXBExtensions.class);
    	    if (ext != null) {
    	    	    configureExt(ext);
    	    }
	    	    
    	    FileCollection fc = this.getXjcClasspath();
    	    XJC2Task task = new XJC2Task();
    	    task.setProject(getAnt().getAntProject());
    	    Path taskCp = task.createClasspath();
    	    println "task classpath $taskCp"
    	    println "task classpath $fc"
    	    for (File dep : fc) {
    	    	    println "dependency $dep"
    	    	    taskCp.createPathElement().setLocation(dep);
    	    }
    	    
    	    FileTree ft = this.getSource();	    
    	    for(File f:ft.getFiles()) {
    	    	    task.setSchema(f.getAbsolutePath());
    	    }
    	    
    	    task.setPackage(this.packageName);
    	    task.setDestdir(this.outputDirectory);
    	    task.setRemoveOldOutput(true);
    
	    task.execute();
	    
	    new File(this.outputDirectory,"JAXB."+getName()+".generated").createNewFile();
    }
}
