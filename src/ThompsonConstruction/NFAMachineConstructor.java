package ThompsonConstruction;

import java.util.Set;

public class NFAMachineConstructor {

    private Lexer lexer;
    private NFAManager nfaManager = null;

    public NFAMachineConstructor(Lexer lexer) throws Exception {
        this.lexer = lexer;
        nfaManager = new NFAManager();

        while (lexer.MatchToken(Lexer.Token.EOS)){
            lexer.advance();
        }
    }

    public void factor(NFAPair pairOut) throws Exception {
        boolean handled = constructStarClosure(pairOut);
        if(!handled)
            handled = constructPlusClosure(pairOut);
        if(!handled)
            handled = constructOptionsClosure(pairOut);
    }

    public boolean constructStarClosure(NFAPair pairOut) throws Exception {
        term(pairOut);

        if(!lexer.MatchToken(Lexer.Token.CLOSURE))
            return false;

        NFA start = nfaManager.newNFA();
        NFA end = nfaManager.newNFA();

        start.next = pairOut.startNode;
        pairOut.endNode.next = end;

        start.next2 = end;
        pairOut.endNode.next2 = start;

        pairOut.startNode = start;
        pairOut.endNode = end;

        return true;
    }

    public boolean constructPlusClosure(NFAPair pairOut) throws Exception {
        /*
         * term+
         */
        term(pairOut);

        if (!lexer.MatchToken(Lexer.Token.PLUS_CLOSE))
            return false;

        NFA start = nfaManager.newNFA();
        NFA end = nfaManager.newNFA();

        start.next = pairOut.startNode;
        pairOut.endNode.next = end;

        pairOut.endNode.next2 = start;

        pairOut.startNode = start;
        pairOut.endNode = end;

        return true;
    }

    public boolean constructOptionsClosure(NFAPair pairOut) throws Exception {
        term(pairOut);

        if(!lexer.MatchToken(Lexer.Token.OPTIONAL))
            return false;

        NFA start = nfaManager.newNFA();
        NFA end = nfaManager.newNFA();

        start.next = pairOut.startNode;
        pairOut.endNode.next = end;

        start.next2 = end;

        pairOut.startNode = start;
        pairOut.endNode = end;

        return true;
    }

    private void term(NFAPair pairOut) throws Exception {
        boolean handled = constructNfaForSingleCharacter(pairOut);
        if (!handled) 
            handled = constructNfaForDot(pairOut);
        if (!handled) 
            handled = constructNFAForCharacterSet(pairOut);
    }

    private boolean constructNfaForSingleCharacter(NFAPair pairOut) throws Exception {
        if(!lexer.MatchToken(Lexer.Token.L)) return false;

        NFA start = pairOut.startNode = nfaManager.newNFA();
        pairOut.endNode = pairOut.startNode.next = nfaManager.newNFA();

        start.setEdge(lexer.getLexeme());
        lexer.advance();

        return true;
    }

    public boolean constructNfaForDot(NFAPair pairOut) throws Exception {
        if (!lexer.MatchToken(Lexer.Token.ANY)) {
            return false;
        }

        NFA start = pairOut.startNode = nfaManager.newNFA();
        pairOut.endNode = pairOut.startNode.next = nfaManager.newNFA();

        start.setEdge(NFA.CCL);
        start.addToSet((byte)'\n');
        start.addToSet((byte)'\r');
        start.setComplement();

        lexer.advance();

        return true;
    }

    private boolean constructNFAForCharacterSetWithoutNegative(NFAPair pairOut) throws Exception {
        if(!lexer.MatchToken(Lexer.Token.CCL_START)) return false;

        lexer.advance();

        NFA start = pairOut.startNode = nfaManager.newNFA();
        pairOut.endNode = pairOut.startNode.next = nfaManager.newNFA();

        start.setEdge(NFA.CCL);

        if(!lexer.MatchToken(Lexer.Token.CCL_END))
            dodash(start.inputSet);

        if(!lexer.MatchToken(Lexer.Token.CCL_END))
            ErrorHandler.parseError(ErrorHandler.Error.E_BAD_EXPR);

        lexer.advance();

        return true;
    }

    private boolean constructNFAForCharacterSet(NFAPair pairOut) throws Exception {
        if (!lexer.MatchToken(Lexer.Token.CCL_START)) {
            return false;
        }

        lexer.advance();
        boolean negative = false;
        if (lexer.MatchToken(Lexer.Token.AT_BOL)) {
            negative = true;
        }

        NFA start = null;
        start = pairOut.startNode = nfaManager.newNFA();
        pairOut.endNode = pairOut.startNode.next = nfaManager.newNFA();
        start.setEdge(NFA.CCL);

        if (!lexer.MatchToken(Lexer.Token.CCL_END)) {
            dodash(start.inputSet);
        }

        if (!lexer.MatchToken(Lexer.Token.CCL_END)) {
            ErrorHandler.parseError(ErrorHandler.Error.E_BAD_EXPR);
        }

        if (negative) {
            start.setComplement();
        }

        lexer.advance();

        return true;
    }
    private void dodash(Set<Byte> set) {
        int first = 0;

        while (!lexer.MatchToken(Lexer.Token.EOS) && !lexer.MatchToken(Lexer.Token.CCL_END)) {
            if (!lexer.MatchToken(Lexer.Token.DASH)) {
                first = lexer.getLexeme();
                set.add((byte)first);
            }
            else {
                lexer.advance(); //越过 -
                while (first <= lexer.getLexeme()) {
                    set.add((byte)first);
                    first++;
                }
            }
            lexer.advance();
        }
    }
}































