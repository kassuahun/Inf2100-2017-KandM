package no.uio.ifi.asp.main;

import no.uio.ifi.asp.parser.AspExpr;
import no.uio.ifi.asp.parser.AspProgram;
import no.uio.ifi.asp.runtime.RuntimeLibrary;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import static no.uio.ifi.asp.scanner.TokenKind.eofToken;
import static no.uio.ifi.asp.scanner.TokenKind.newLineToken;

public class Main {
    public static final String version = "2017-08-22";

    public static LogFile log = null;

    public static void main(String arg[]) {
	String fileName = null, baseFilename = null;
	boolean testExpr = false, testParser = false, testScanner = false,
	    logE = false, logP = false, logS = false, logY = false;

	System.out.println("This is the Ifi Asp interpreter (" +
			   version + ")");

	for (int i = 0;  i < arg.length;  i++) {
	    String a = arg[i];

	    if (a.equals("-logE")) {
		logE = true;
	    } else if (a.equals("-logP")) {
		logP = true;
	    } else if (a.equals("-logS")) {
		logS = true;
	    } else if (a.equals("-logY")) {
		logY = true;
	    } else if (a.equals("-testexpr")) {
		testExpr = true; 
	    } else if (a.equals("-testparser")) {
		testParser = true; 
	    } else if (a.equals("-testscanner")) {
		testScanner = true; 
	    } else if (a.startsWith("-")) {
		usage();
	    } else if (fileName != null) {
		usage();
	    } else {
		fileName = a;
	    }
	}
	if (fileName == null) usage();

	baseFilename = fileName;
	if (baseFilename.endsWith(".asp"))
	    baseFilename = baseFilename.substring(0,baseFilename.length()-4);
	else if (baseFilename.endsWith(".py"))
	    baseFilename = baseFilename.substring(0,baseFilename.length()-3);

	log = new LogFile(baseFilename+".log");
	if (logE || testExpr) log.doLogEval = true;
	if (logP || testParser) log.doLogParser = true;
	if (logS || testScanner) log.doLogScanner = true;
	if (logY || testExpr || testParser) log.doLogPrettyPrint = true;

	Scanner s = new Scanner(fileName); // create linenumber class to store the buffer from the line. the file reading ends here.
	//System.out.println("scanner done");
	//s.printer();
	if (testScanner)
	    doTestScanner(s);
	else if (testParser)
	    doTestParser(s);
	else if (testExpr)
	    doTestExpr(s);
	else
	    doRunInterpreter(s);

	if (log != null) log.finish();
	//file compare
		filecompare(baseFilename);

	System.exit(0);
    }

	public static void filecompare(String baseFilename)
	{
		File in = new File(baseFilename+".log");
		File in1 = new File(baseFilename+"ref.log");
		BufferedReader br = null;
		BufferedReader br1= null;
		try{

			br = new BufferedReader(new FileReader(in));
			br1 = new BufferedReader(new FileReader(in1));
			String strLine, strLine1;
			int i = 1;
			int errorCount=0;
			//Read File Line By Line
			while (((strLine = br.readLine()) != null) && ((strLine1 = br1.readLine()) != null))   {
				if (!strLine.equals(strLine1)) {
					System.out.println("line no " + i + " Error\n \t" + strLine + "\n \t"+ strLine1);
					errorCount++;
				}
				i++;
			}
			System.out.println("File compare has done\n" +
					"[ " + errorCount + " Errors Found ]");
			//Close the input stream
			br.close();
			br1.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

    private static void doTestScanner(Scanner s) {
	do {
	    s.readNextToken();
	} while (s.curToken().kind != eofToken); 
    }


    private static void doTestParser(Scanner s) {
	AspProgram prog = AspProgram.parse(s);
	if (log.doLogPrettyPrint)
	    prog.prettyPrint();
    }


    private static void doTestExpr(Scanner s) {
	ArrayList<AspExpr> exprs = new ArrayList<>();
	while (s.curToken().kind != eofToken) {
	    exprs.add(AspExpr.parse(s));
	    AspExpr.skip(s, newLineToken);
	}

	RuntimeScope emptyScope = new RuntimeScope();
	for (AspExpr e: exprs) {
	    e.prettyPrint();  log.prettyWriteLn(" ==>");
	    try {
		RuntimeValue res = e.eval(emptyScope);
		log.traceEval(res.showInfo(), e);
	    } catch (RuntimeReturnValue rrv) {
		panic("Uncaught return value!");
	    }
	}
    }


    private static void doRunInterpreter(Scanner s) {
	AspProgram prog = AspProgram.parse(s);
	if (log.doLogPrettyPrint) 
	    prog.prettyPrint();

	RuntimeScope lib = new RuntimeLibrary();
	RuntimeScope globals = new RuntimeScope(lib);
	try {
	    prog.eval(globals);
	} catch (RuntimeReturnValue rrv) {
	    panic("Uncaught return value!");
	}
    }


    public static void error(String message) {
	System.out.println();
	System.err.println(message);
	if (log != null) log.noteError(message);
	System.exit(1);
    }


    public static void panic(String message, int lineNum) {
	String m = "*** ASP PANIC";
	if (lineNum > 0) m += " ON LINE " + lineNum;
	m += " ***: " + message;
	error(m);
    }


    public static void panic(String message) {
	panic(message, 0);
    }


    private static void usage() {
	error("Usage: java -jar asp.jar " +
	    "[-log{E|P|S|Y}] [-test{expr|parser|scanner}] file");
    }
}
