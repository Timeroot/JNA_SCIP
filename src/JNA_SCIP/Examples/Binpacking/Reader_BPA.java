package JNA_SCIP.Examples.Binpacking;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import com.sun.jna.ptr.IntByReference;

import JNA_SCIP.*;

public class Reader_BPA {

	static final String READER_NAME = "bpareader";
	static final String READER_DESC = "file reader for binpacking data format";
	static final String READER_EXTENSION = "bpa";
	
	//permanently bind the callback
	static final SCIP_DECL_READERREAD BPAreaderRead = Reader_BPA::readerread;
	static SCIP_RETCODE readerread(SCIP scip, SCIP_READER reader, String filename, IntByReference result) {
		
		result.setValue(SCIP_RESULT.SCIP_DIDNOTRUN.ordinal());
		
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
			//name of the problem
			String name = br.readLine();
			scip.debugMsg("problem name <%s>\n", name);
			
			//dimensions
			String line = br.readLine().strip();
			String[] dimLineParts = line.split(" ");
			
			long capacity = Integer.valueOf(dimLineParts[0]);
			int nitems = Integer.valueOf(dimLineParts[1]);
			
			String bestsolvalue = dimLineParts.length > 2 ? dimLineParts[2] : "unknown";
			scip.debugMsg("capacity = <%d>, number of items = <%d>, best known solution = %s\n",
					capacity, nitems, bestsolvalue);
			
			long[] weights = new long[nitems];
			int[] ids = new int[nitems];
			
			Scanner sc = new Scanner(br);
			for(int i=0; i<nitems; i++) {
				long weight = sc.nextLong();
				scip.debugMsg("found weight %d <%lld>\n", i, weight);
				weights[i] = weight;
				ids[i] = i;
			}
			sc.close();
			
			ProbdataBinpacking.create(scip, name, ids, weights, nitems, capacity);
			
		} catch (FileNotFoundException e) {
			JSCIP.messagePrintError("Could not open file <"+filename+">.\n"+e.getMessage()+"\n");
			throw new ScipException(SCIP_RETCODE.SCIP_NOFILE);
		} catch (IOException e) {
			JSCIP.messagePrintError("Error reading file <"+filename+">.\n"+e.getMessage()+"\n");
			throw new ScipException(SCIP_RETCODE.SCIP_READERROR);
		} catch(ScipException ex) {
			System.err.println("ScipException: "+ex);
			return ex.retcode;
		}
		
		result.setValue(SCIP_RESULT.SCIP_SUCCESS.ordinal());
		return SCIP_RETCODE.SCIP_OKAY;
	}
	
	static void includeReaderBPA(SCIP scip) {
		SCIP_READER reader = scip.includeReaderBasic(READER_NAME, READER_DESC, READER_EXTENSION, null);
		scip.setReaderRead(reader, BPAreaderRead);
	}
}
