package com.example.scientificcalculatorjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    EditText inpuOfUser;
    TextView finalResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inpuOfUser = findViewById(R.id.textToParse);
        finalResult = findViewById(R.id.tex);
    }

    public void onClickButton(View view){
        inpuOfUser = findViewById(R.id.textToParse);
        finalResult = findViewById(R.id.tex);
        if(inpuOfUser.getText().toString().isEmpty()) return;
        finalResult.setText((compute(inpuOfUser.getText().toString())).toString());
    }
    public Double compute(String text){
        return evalPostFix(parse(lexicalAnalysis(text)));
    }
    enum tokenType{
        NUM,MINUS,PLUS,MUL,DIV,LPARAN,RPARAN,UNARY,COS,SIN,ABS,SINH,COSH,LN,LOG
    }

    public class Token{
        public tokenType type;
        public String lexeme;
        public Token(tokenType type, String lexeme){
            this.type = type;
            this.lexeme = lexeme;
        }
        public String get_lexeme(){
            return  this.lexeme;
        }
        public tokenType getType(){
            return this.type;
        }
    }
    public class analyze{
        public String inp;
        ArrayList<Token> result = new ArrayList<>();;
        public analyze(String inp){
            this.inp = inp;
        }
        public void addToken(tokenType type, String lexeme){

            result.add(new Token(type,lexeme));
        }

        int current = 0;
        void scan(){
            while(current< inp.length()){
                char c = inp.charAt(current);
                if (c == '(') addToken(tokenType.LPARAN,"(");
                else if (c == ')') addToken(tokenType.RPARAN,")");
                else if (c == '+') addToken(tokenType.PLUS,"+");
                else if (c == '*') addToken(tokenType.MUL,"*");
                else if (c == '/') addToken(tokenType.DIV,"/");
                else if(c=='-'){
                    if (current != 0 && Character.isDigit(inp.charAt(current-1)))
                    addToken(tokenType.MINUS,"-");
                    else if (  current == 0 || !Character.isDigit(inp.charAt(current-1)))
                    addToken(tokenType.UNARY,"u");
                }
                else if (Character.isDigit(c)){
                    StringBuilder temp = new StringBuilder(String.valueOf(c));
                    int counter = current + 1;
                    while(counter<= inp.length()-1){
                        if(!(Character.isDigit(inp.charAt(counter))) && inp.charAt(counter)!= '.'){
                            break;
                        }
                        temp.append(String.valueOf(inp.charAt(counter)));
                        counter+=1;
                    }
                    current = counter - 1;
                    addToken(tokenType.NUM,temp.toString());
                }
                else if (c=='c'){
                    StringBuilder temp = new StringBuilder(String.valueOf(c));
                    int counter = current + 1;
                    while(counter<= inp.length()-1){
                        if(!(Character.isLetter(inp.charAt(counter)))){
                            break;
                        }
                        temp.append(String.valueOf(inp.charAt(counter)));
                        counter+=1;
                    }
                    current = counter - 1;
                    addToken(tokenType.COS,temp.toString());
                }


                else if (c=='s'){
                    StringBuilder temp = new StringBuilder(String.valueOf(c));
                    int counter = current + 1;
                    while(counter<= inp.length()-1){
                        if(!(Character.isLetter(inp.charAt(counter)))){
                            break;
                        }
                        temp.append(String.valueOf(inp.charAt(counter)));
                        counter+=1;
                    }
                    current = counter - 1;
                    addToken(tokenType.SIN,temp.toString());
                }
                else if (c=='a'){
                    StringBuilder temp = new StringBuilder(String.valueOf(c));
                    int counter = current + 1;
                    while(counter<= inp.length()-1){
                        if(!(Character.isLetter(inp.charAt(counter)))){
                            break;
                        }
                        temp.append(String.valueOf(inp.charAt(counter)));
                        counter+=1;
                    }
                    current = counter - 1;
                    addToken(tokenType.ABS,temp.toString());
                }

                else if (c=='l'){
                    StringBuilder temp = new StringBuilder(String.valueOf(c));
                    int counter = current + 1;
                    while(counter<= inp.length()-1){
                        if(!(Character.isLetter(inp.charAt(counter)))){
                            break;
                        }
                        temp.append(String.valueOf(inp.charAt(counter)));
                        counter+=1;
                    }
                    current = counter - 1;
                    if (temp.toString().equals("log"))
                         addToken(tokenType.LOG,temp.toString());
                    else if (temp.toString().equals("logn")) addToken(tokenType.LN,temp.toString());
                }
                current +=1;
            }
        }
    }
public ArrayList<Token> lexicalAnalysis(String text){
        analyze temp = new analyze(text);
        temp.scan();
        return temp.result;
    }
public boolean isOperator(String text){
    return text.equals("+") || text.equals("-") || text.equals("*") || text.equals("/") || text.equals("u") || text.equals("cos") || text.equals("sin") || text.equals("log") || text.equals("logn") || text.equals("abs");
}
public int getPrecedence(String h){
    if (h.equals("+") || h.equals("-")) return 1;
    else if (h.equals("*") || h.equals("/")) return 2;
    else if (h.equals("^") || h.equals("u") || h.equals("cos") || h.equals("sin") || h.equals("log") || h.equals("logn") || h.equals("abs")) return 3;
    return -1;
}
public ArrayList<String> parse(ArrayList<Token> param){
        ArrayList<Token> input = new ArrayList<>(param);
        int counter = 0;
        Stack<String> opStack = new Stack<>();
        ArrayList<String> output = new ArrayList<>();
    while (counter<input.size()) {
        if (input.get(counter).type == tokenType.NUM)
            output.add(input.get(counter).get_lexeme());
            else if (isOperator(input.get(counter).lexeme)) {
            while (!opStack.isEmpty() && getPrecedence(input.get(counter).get_lexeme()) <= getPrecedence(opStack.peek())) {
                output.add(opStack.pop());
            }
            opStack.push(input.get(counter).get_lexeme());
        }
        else if (input.get(counter).get_lexeme().equals("("))
            opStack.push(input.get(counter).get_lexeme());
            else if (input.get(counter).get_lexeme().equals(")")) {
            while (!opStack.isEmpty() && !opStack.peek().equals("(")) {
                output.add(opStack.pop());
            }
            opStack.pop();
        }
        counter+=1;
    }
    while (!opStack.isEmpty()) {
        output.add(opStack.pop());
    }
    return output;
}
public boolean isDigitPostEval(String str){
    for (int i = 0; i < str.length(); i++) {
        if (Character.isDigit(str.charAt(i))){
            return true;
        }

    }
    return false;
}
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
public Double evalPostFix(ArrayList<String> inp){
    ArrayList<String> input = new ArrayList<>(inp);
    Stack<Double>opStack = new Stack<>();
    for (int i = 0; i < input.size(); i++) {
        String c = String.valueOf(input.get(i));
        if(isNumeric(c)){
            opStack.push(Double.valueOf(c));
        }
        else if(c.equals("u")){
            Double v = opStack.peek()*-1;
            opStack.pop();
            opStack.push(v);
        }
        else if(c.equals("cos")){
            Double v = Math.cos(opStack.peek());
            opStack.pop();
            opStack.push(v);
        }
        else if(c.equals("sin")){
            Double v = Math.sin(opStack.peek());
            opStack.pop();
            opStack.push(v);
        }

        else if(c.equals("log")){
            Double v = Math.log10(opStack.peek());
            opStack.pop();
            opStack.push(v);
        }
        else if(c.equals("logn")){
            Double v = Math.log(opStack.peek());
            opStack.pop();
            opStack.push(v);
        }
        else if(c.equals("abs")){
            Double v = Math.abs(opStack.peek());
            opStack.pop();
            opStack.push(v);
        }
        else{
            Double firstValue = opStack.pop();
            Double secondValue = opStack.pop();
            switch (c) {
                case "+":
                    opStack.push(secondValue + firstValue);
                    break;
                case "*":
                    opStack.push(secondValue * firstValue);
                    break;
                case "/":
                    opStack.push(secondValue / firstValue);
                    break;
                case "-":
                    opStack.push(secondValue - firstValue);
                    break;
            }
        }
    }
    return opStack.pop();
}

}