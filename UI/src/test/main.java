package test;

import program.Program;

public class main {

    static public String eithanPath = "C:\\Users\\eitan\\OneDrive\\שולחן העבודה\\Computer Science Degree\\Java\\badic.xml";
    static public String galPath = "/Users/galrubinstein/Downloads/badic.xml";

    public static void main(String[] args) {
        fullTest2();

    }

    private static void fullTest1(){
        Program program = null;
        try {
            program = new Program(galPath);
            program.runProgram(1,2);
            program.runProgram(5,4);
            program.getVariables().forEach(System.out::println);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void fullTest2(){
        Program program = null;
        try {
            program = new Program(eithanPath);
            program.runProgram(5,2);
            program.getVariables().forEach(System.out::println);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void fullTestEitan3(){
        Program program = null;
        try {
            program = new Program(eithanPath);
            program.runProgram(1,2);
            program.runProgram(5,4);
            program.getVariables().forEach(System.out::println);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
