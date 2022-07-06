package JNA_SCIP.Examples.LOP;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import com.sun.jna.ptr.IntByReference;

import JNA_SCIP.*;

public class Reader_LOP {

	static final String READER_NAME = "lopreader";
	static final String READER_DESC = "file reader for linear ordering problems";
	static final String READER_EXTENSION = "lop";
	
	//permanently bind the callback
	static final SCIP_DECL_READERREAD LOPreaderRead = Reader_LOP::readerread;
	static SCIP_RETCODE readerread(SCIP scip, SCIP_READER reader, String filename, IntByReference result) {
		try {
			
			scip.infoMessage(null, "File name:\t\t"+filename+"\n");
			
			//read file
			Reader_LOP lop = new Reader_LOP(scip, filename);
			lop.make_LOP_problem();
			
		} catch(ScipException ex) {
			System.err.println("ScipException: "+ex);
			return ex.retcode;
		}
		result.setValue(SCIP_RESULT.SCIP_SUCCESS.ordinal());
		return SCIP_RETCODE.SCIP_OKAY;
	}
	
	static void includeReaderLOP(SCIP scip) {
		SCIP_READER reader = scip.includeReaderBasic(READER_NAME, READER_DESC, READER_EXTENSION, null);
		scip.setReaderRead(reader, LOPreaderRead);
	}
	
	//Actual class that contains the data for the problem
	SCIP scip;
	String filename;
	int n;
	double[][] W;
	SCIP_VAR[][] vars;
	
	public Reader_LOP(SCIP scip, String filename) {
		this.scip = scip;
		this.filename = filename;
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
			
			String line;
			do{//skip over comment lines
				line = br.readLine();
			} while(!line.matches(" *[0-9]+ *"));
			//we found a line with a single integer
			n = Integer.valueOf(line.strip());
			if(n <= 0) {
				JSCIP.messagePrintError("Reading of number of elements failed.\n");
				throw new ScipException(SCIP_RETCODE.SCIP_READERROR);
			}
			W = new double[n][n];
			Scanner sc = new Scanner(br);
			for(int i=0; i<n; i++) {
				for(int j=0; j<n; j++) {
					W[i][j] = sc.nextInt();
				}
			}
			sc.close();
			
		} catch (FileNotFoundException e) {
			JSCIP.messagePrintError("Could not open file <"+filename+">.\n"+e.getMessage()+"\n");
			throw new ScipException(SCIP_RETCODE.SCIP_NOFILE);
		} catch (IOException e) {
			JSCIP.messagePrintError("Error reading file <"+filename+">.\n"+e.getMessage()+"\n");
			throw new ScipException(SCIP_RETCODE.SCIP_READERROR);
		}
	}
	
	public void make_LOP_problem() {
		String problemName = getProblemName(filename);
		
		scip.infoMessage(null, "Problem name:\t\t"+problemName+"\n");
		scip.infoMessage(null, "Number of elements:\t\t"+n+"\n");
		
		scip.createProbBasic(problemName);
		
		scip.setObjsense(SCIP_OBJSENSE.MAXIMIZE);
		
		vars = new SCIP_VAR[n][n];
		for(int i=0; i<n; i++) {
			for(int j=0; j<n; j++) {
				if(i==j) {
					vars[i][j] = null;
				} else {
					String varname = "x#"+i+"#"+j;
					SCIP_VAR var = scip.createVarBasic(varname, 0, 1.0, W[i][j], SCIP_VARTYPE.BINARY);
					scip.addVar(var);
					vars[i][j] = var;
				}
			}
		}
		
		SCIP_CONS cons_lop = Conshdlr_LOP.createConsLOP(scip, "LOP", vars,
				true, true, true, true, true, false, false, false, false, false);
		scip.addCons(cons_lop);
		scip.releaseCons(cons_lop);
		
		if ( n <= 10 ) {
			scip.printOrigProblem(null, null, false);
			scip.infoMessage(null, "\n");
		}
		
		for(int i=0; i<n; i++) {
			for(int j=0; j<n; j++) {
				if(i!=j) {
					scip.releaseVar(vars[i][j]);
					vars[i][j] = null;
				}
			}
		}
	}
	
	static String getProblemName(String filename) {
		//trim off directories
		if(filename.contains("/"))
			filename = filename.substring(1+filename.lastIndexOf("/"));
		if(filename.contains("\\"))
			filename = filename.substring(1+filename.lastIndexOf("\\"));
		//trim off extension
		if(filename.contains("."))
			filename = filename.substring(0, filename.lastIndexOf("."));
		return filename;
	}
}
