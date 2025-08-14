package ui;

import Engine.Program;

public class main {
    public static void main(String[] args) {
        fullTest1();

    }

    private static void fullTest1(){
        Program program = new Program();
        //program.loadProgram("/Users/galrubinstein/Downloads/minus.xml");
        program.loadProgram("C:\\Users\\eitan\\OneDrive\\שולחן העבודה\\Computer Science Degree\\Java\\badic.xml");
        program.runProgram(1);
        program.getVariables().forEach(System.out::println);
    }

    private static void fullTest2(){
        Program program = new Program();

    }
}
