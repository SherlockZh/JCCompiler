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

    public void expr(NFAPair pairOut) throws Exception{
        cat_expr(pairOut);

        NFAPair pairLocal = new NFAPair();

        while(lexer.MatchToken(Lexer.Token.OR)){
            lexer.advance();
            cat_expr(pairLocal);

            NFA startNode = nfaManager.newNFA();
            startNode.next2 = pairLocal.startNode;
            startNode.next = pairOut.startNode;
            pairOut.startNode = startNode;

            NFA endNode = nfaManager.newNFA();
            pairOut.endNode.next = endNode;
            pairLocal.endNode.next = endNode;
            pairOut.endNode = endNode;
        }
    }

    public void cat_expr(NFAPair pairOut) throws Exception {
        if(first_in_cat(lexer.getCurrentToken()))
            factor(pairOut);
        while (first_in_cat(lexer.getCurrentToken())){
            NFAPair pairLocal = new NFAPair();
            factor(pairLocal);

            pairOut.endNode.next = pairLocal.startNode;

            pairOut.endNode = pairLocal.endNode;
        }
    }

    private boolean first_in_cat(Lexer.Token token) throws Exception {
        switch (token){
            case CLOSE_PAREN:
            case AT_EOL:
            case OR:
            case EOS:
                return false;
            case CLOSURE:
            case PLUS_CLOSE:
            case OPTIONAL:
                ErrorHandler.parseError(ErrorHandler.Error.E_CLOSE);
                return false;
            case CCL_END:
                ErrorHandler.parseError(ErrorHandler.Error.E_BRACKET);
                return false;
            case AT_BOL:
                ErrorHandler.parseError(ErrorHandler.Error.E_BOL);
                return false;
        }
        return true;
    }

    public void factor(NFAPair pairOut) throws Exception {
        term(pairOut);

        boolean handled = constructStarClosure(pairOut);
        if(!handled)
            handled = constructPlusClosure(pairOut);
        if(!handled)
            handled = constructOptionsClosure(pairOut);
    }

    public boolean constructStarClosure(NFAPair pairOut) throws Exception {
        if(!lexer.MatchToken(Lexer.Token.CLOSURE))
            return false;

        NFA start = nfaManager.newNFA();
        NFA end = nfaManager.newNFA();

        start.next = pairOut.startNode;
        pairOut.endNode.next = pairOut.startNode;

        start.next2 = end;
        pairOut.endNode.next2 = end;

        pairOut.startNode = start;
        pairOut.endNode = end;

        lexer.advance();

        return true;
    }

    public boolean constructPlusClosure(NFAPair pairOut) throws Exception {
        /*
         * term+
         */
        if (!lexer.MatchToken(Lexer.Token.PLUS_CLOSE))
            return false;

        NFA start = nfaManager.newNFA();
        NFA end = nfaManager.newNFA();

        start.next = pairOut.startNode;
        pairOut.endNode.next2 = end;
        pairOut.endNode.next = pairOut.startNode;

        pairOut.startNode = start;
        pairOut.endNode = end;

        lexer.advance();

        return true;
    }

    public boolean constructOptionsClosure(NFAPair pairOut) throws Exception {
        if(!lexer.MatchToken(Lexer.Token.OPTIONAL))
            return false;

        NFA start = nfaManager.newNFA();
        NFA end = nfaManager.newNFA();

        start.next = pairOut.startNode;
        pairOut.endNode.next = end;

        start.next2 = end;

        pairOut.startNode = start;
        pairOut.endNode = end;

        lexer.advance();

        return true;
    }

    public void term(NFAPair pairOut) throws Exception {
        boolean handled = constructExprInParen(pairOut);
        if(!handled)
            handled = constructNfaForSingleCharacter(pairOut);
        if (!handled)
            handled = constructNfaForDot(pairOut);
        if (!handled)
            handled = constructNFAForCharacterSet(pairOut);
    }

    public boolean constructExprInParen(NFAPair pairOut) throws Exception{
        if(lexer.MatchToken(Lexer.Token.OPEN_PAREN)){
            lexer.advance();
            expr(pairOut);
            if(lexer.MatchToken(Lexer.Token.CLOSE_PAREN))
                lexer.advance();
            else
                ErrorHandler.parseError(ErrorHandler.Error.E_PAREN);
            return true;
        }
        return false;
    }

    public boolean constructNfaForSingleCharacter(NFAPair pairOut) throws Exception {
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

    public boolean constructNFAForCharacterSetWithoutNegative(NFAPair pairOut) throws Exception {
        if(!lexer.MatchToken(Lexer.Token.CCL_START)) return false;

        lexer.advance();

//        constructNFAForCharacterSetHelper(pairOut);

        NFA start = pairOut.startNode = nfaManager.newNFA();
        pairOut.endNode = pairOut.startNode.next = nfaManager.newNFA();
        start.setEdge(NFA.CCL);

        if(!lexer.MatchToken(Lexer.Token.CCL_END))
            doDash(start.inputSet);

        if(!lexer.MatchToken(Lexer.Token.CCL_END))
            ErrorHandler.parseError(ErrorHandler.Error.E_BAD_EXPR);

        lexer.advance();

        return true;
    }

//    public NFA constructNFAForCharacterSetHelper(NFAPair pairOut) throws Exception {
//        NFA start = pairOut.startNode = nfaManager.newNFA();
//        pairOut.endNode = pairOut.startNode.next = nfaManager.newNFA();
//        start.setEdge(NFA.CCL);
//
//        if(!lexer.MatchToken(Lexer.Token.CCL_END))
//            doDash(start.inputSet);
//
//        if(!lexer.MatchToken(Lexer.Token.CCL_END))
//            ErrorHandler.parseError(ErrorHandler.Error.E_BAD_EXPR);
//
//        return start;
//    }

    public boolean constructNFAForCharacterSet(NFAPair pairOut) throws Exception {
        if (!lexer.MatchToken(Lexer.Token.CCL_START)) {
            return false;
        }

        lexer.advance();
        boolean negative = false;
        if (lexer.MatchToken(Lexer.Token.AT_BOL)) {
            negative = true;
        }
//        NFA start = constructNFAForCharacterSetHelper(pairOut);
        NFA start = pairOut.startNode = nfaManager.newNFA();
        pairOut.endNode = pairOut.startNode.next = nfaManager.newNFA();
        start.setEdge(NFA.CCL);

        if (!lexer.MatchToken(Lexer.Token.CCL_END)) {
            doDash(start.inputSet);
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
    public void doDash(Set<Byte> set) {
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






























