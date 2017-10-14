package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.TokenKind;

import static no.uio.ifi.asp.scanner.TokenKind.colonToken;

/**
 *
 */

public class AspWhileStmt extends AspStmt{
    AspExpr condition;
    AspSuite boady;

    public AspWhileStmt(int i) {
        super(i);
    }

    public static AspWhileStmt parse(Scanner s) {
        Main.log.enterParser("AspWhileStmt");

        AspWhileStmt ws = new AspWhileStmt(s.curLineNum());
        skip(s, TokenKind.whileToken);
        ws.condition=AspExpr.parse(s);
        skip(s, colonToken);
        ws.boady = AspSuite.parse(s);

        Main.log.leaveParser("AspWhileStmt");
        return ws;

    }

    @Override
    public void prettyPrint() {

    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return null;
    }
}



