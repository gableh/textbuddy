import java.io.*;
import java.util.Scanner;
public class TextBuddy {
	private static boolean isRunning = true;
	private static String txtFile;
	private static String cmd;
	private static BufferedReader reader,tempReader;
	private static BufferedWriter writer,tempWriter;
	private static Scanner sc;
	private static int count;
	private static String[] Commands;
	private static final String MESSAGE_ISEMPTY ="%1$s is empty.";
	private static final String MESSAGE_WELCOME ="Welcome to TextBuddy. %1$s is ready for use.";
	private static final String MESSAGE_NOFILE ="Since no input file has been specified,one has been created for you";
	private static final String MESSAGE_DELETE ="Deleted %1$s from %2$s";
	private static final String MESSAGE_GOODBYE = "Goodbye";
	private static final String MESSAGE_ADDLINE = "added to %1$s : %2$s";
	private static final String MESSAGE_CLEAR = "all content deleted from %1$s";
	/**
	 * Main function,requires a file name to be specified as its params.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		
		File file = toBeEdited(args);
		sc = new Scanner(System.in);

		while(isRunning){
			Commands = getCommand(sc);
			parseCommand(Commands, file);
		}
		sc.close();
	}
	/**
	 * Parses command entered by user,will ignore all other commands besides
	 * ADD,DELETE,CLEAR,DISPLAY and EXIT.Commands are case insensitive.
	 * @param Commands
	 * @param file
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void parseCommand(String[] Commands, File 
			file)throws IOException, FileNotFoundException {
		String command = Commands[0].toLowerCase();
		if(command.equals("exit")){
			stopRunning();
			displayMessage(MESSAGE_GOODBYE);
			System.exit(0);
		}
		if(command.equals("add")){
			addNewLine(Commands, file);
		}
		if(command.equals("clear")){
			clearAll(file);
		}
		if(command.equals("display")){
			displayAll(file);
		}
		if(Commands[0].toLowerCase().equals("delete")){
			int lineToDelete = Integer.parseInt(Commands[1]);
			deleteLine(file,lineToDelete);		
		}
	}
	/**
	 * adds a new line to the file,will ignore extra white space
	 * @param Commands
	 * @param file
	 * @throws IOException
	 */
	private static void addNewLine(String[] Commands, File file) throws IOException {
		String toBeAdded = "";
		for(int i=1;i<Commands.length;i++){
			toBeAdded=toBeAdded + Commands[i]+" ";
		}
		addLine(toBeAdded.trim(),file,file.getName());
	}
	/**
	 * exits the application
	 */
	private static void stopRunning() {
		isRunning = false;
	}
	/**
	 * Opens the file to be edited,a new file "textbuddy.txt" will be created if no file specified.
	 * @param args
	 * @return file
	 * @throws IOException
	 */
	private static File toBeEdited(String[] args) throws IOException {
		if(!hasInput(args)){
			txtFile = "textbuddy.txt";
			displayMessage(MESSAGE_NOFILE);
		}else{
			txtFile = args[0];
		}
		File file = accessFile(txtFile);
		displayMessage(String.format(MESSAGE_WELCOME,txtFile));
		return file;
	}
	/**
	 * Checks if user has entered a file name
	 * @param args
	 * @return boolean
	 */
	private static boolean hasInput(String[] args) {
		return args[0]!=null;
	}
	/**
	 * Gets the next Command from the user for parsing
	 * @param sc
	 * @return String[]
	 */
	private static String[] getCommand(Scanner sc) {
		displayCommand();
		cmd = sc.nextLine();
		Commands = cmd.split(" ");
		return Commands;
	}
	/** 
	 *prints "Command: "
	 */
	private static void displayCommand() {
		System.out.print("Command: ");
	}
	/**
	 * Displays a message to the user,message is given by the param string1.
	 * @param string1
	 */
	private static void displayMessage(String string1) {
		System.out.println(string1);
	}
	/**
	 * Attempts to access the file and return a file object
	 * @param args
	 * @return File
	 * @throws IOException
	 */
	private static File accessFile(String args) throws IOException {
		File file = new File(args);
		checkFileExists(file);
		return file;
	}
	/**
	 * Checks if the file exists,will create a new file if it does not.
	 * @param file
	 * @throws IOException
	 */
	private static void checkFileExists(File file) throws IOException {
		if(!file.exists()){
			file.createNewFile();
		}
	}
	/**
	 * Adds a line to the current file
	 * @param toBeAdded
	 * @param file
	 * @param fileName
	 * @throws IOException
	 */
	private static void addLine(String toBeAdded,File file,String fileName)throws IOException{
	
		writer = createWriter(file);
		reader = createReader(file);
		addALine(toBeAdded);
		
		displayMessage(String.format(MESSAGE_ADDLINE, fileName,toBeAdded));
		closeStreams(writer, reader);
	}
	/**
	 * Helper method for AddLine
	 * @param toBeAdded
	 * @throws IOException
	 */
	private static void addALine(String toBeAdded) throws IOException {
		if(getNextLine(reader) ==null){
			writer.write(toBeAdded);
		}else{
			writer.newLine();
			writer.write(toBeAdded);
		}
	}
	/**
	 * Create a BufferedWriter from given file.
	 * @param file
	 * @return BufferedWriter
	 * @throws IOException
	 */
	private static BufferedWriter createWriter(File file) throws IOException {
		return new BufferedWriter(new FileWriter(file.getAbsoluteFile(),true));
	}
	/**
	 * Closes the Writer and Reader.
	 * @param writer
	 * @param reader
	 * @throws IOException
	 */
	private static void closeStreams(BufferedWriter writer, BufferedReader reader)
			throws IOException {
		closeWriter(writer);
		closeReader(reader);
	
	}
	/**
	 * Helper method to close Reader
	 * @param reader
	 * @throws IOException
	 */
	private static void closeReader(BufferedReader reader) throws IOException {
		if(reader!= null){
			reader.close();
			reader = null;
		}
	}
	/**
	 * Helper method to close Writer.
	 * @param writer
	 * @throws IOException
	 */
	private static void closeWriter(BufferedWriter writer) throws IOException {
		if(writer !=null){
			writer.flush();
			writer.close();
			writer=null;
		}
	}
	/**
	 * Adds a line to the temp file,this is for deleteLine() purposes.
	 * @param toBeAdded
	 * @param file
	 * @throws IOException
	 */
	private static void addLine(String toBeAdded,File file)throws IOException{
		tempWriter = createWriter(file);
		tempReader = createReader(file);
		addLineToTempFile(toBeAdded);
		
		closeStreams(tempWriter, tempReader);
	}
	/**
	 * Adds a line to the temp file.
	 * @param toBeAdded
	 * @throws IOException
	 */
	private static void addLineToTempFile(String toBeAdded) throws IOException {
		if(getNextLine(tempReader) ==null){
			tempWriter.write(toBeAdded);
		}else{
			tempWriter.newLine();
			tempWriter.write(toBeAdded);
		}
	}
	/**
	 * Clears the display by creating a new file,deleting the old file and renaming the new file to the name of the
	 *  old file.
	 * @param file
	 * @throws IOException
	 */
	private static void clearAll(File file)throws IOException{
		File newFile = createTempFile();
		BufferedWriter writer = createWriter(file);
		onCompletedDelete(file,newFile);
		closeStreams(writer, null);
		displayMessage(String.format(MESSAGE_CLEAR, file.getName()));
	}
	/**
	 * Displays all lines currently in the file
	 * @param file
	 * @throws IOException
	 */
	private static void displayAll(File file) throws IOException{
		try {
			reader =createReader(file);
			String currentLine = getNextLine(reader);
			count = 1;
			if(isEmpty(currentLine)){
				System.out.println(String.format(MESSAGE_ISEMPTY,file.getName()));
				closeStreams(null,reader);
				return ;
			}
			do{
				currentLine = printAllOtherLines(currentLine);
			}while(currentLine != null);
			closeStreams(null,reader);
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * Prints all other lines,used as a helper method for displayAll();
	 * @param currentLine
	 * @return String
	 * @throws IOException
	 */
	private static String printAllOtherLines(String currentLine)
			throws IOException {
		displayMessage(count++ +". "+currentLine);
		currentLine = getNextLine(reader);
		return currentLine;
	}
	/**
	 * Creates a bufferedReader from the input file.
	 * @param file
	 * @return BufferedReader
	 * @throws FileNotFoundException
	 */
	private static BufferedReader createReader(File file)
			throws FileNotFoundException {
		return new BufferedReader(new FileReader(file));
	}
	/**
	 * Gets next line from the current reader.
	 * @param reader
	 * @return String
	 * @throws IOException
	 */
	private static String getNextLine(BufferedReader reader) throws IOException {
		return reader.readLine();
	}
	/**
	 * Checks if the current line returned by the reader is empty.
	 * @param currentLine
	 * @return boolean
	 */
	private static boolean isEmpty(String currentLine) {
		return currentLine==null;
	}
	/**
	 * Deletes a line from the reader by creating a temp file,writing all lines from the original
	 * file to the temp file except the line to be deleted before deleting the original file and renaming
	 * the temp file to the name of the old file.
	 * @param file
	 * @param toBeDeleted
	 * @throws IOException
	 */
	private static void deleteLine(File file,int toBeDeleted)throws IOException{
		File newFile = createTempFile();
		writer = createWriter(file);
		reader =createReader(file);
		
		
		String line;
		count = 1;
		try {
			while((line =reader.readLine())!= null){			
				beginDeletion(file, toBeDeleted, newFile, line);
			}
			
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		closeStreams(writer,reader);
		file.delete();
		newFile.renameTo(file);
	}
	/** 
	 * Checks if the line is toBeDeleted,will print MESSAGE_DELETE if it is,otherwise
	 * if will add to the temp file.
	 */
	private static void beginDeletion(File file, int toBeDeleted, File newFile,
			String line) throws IOException {
		if(count!= toBeDeleted){
			addLine(line,newFile);
		}else{
			displayMessage(String.format(MESSAGE_DELETE, file.getName(),line));
		}
		count++;
	}
	/**
	 * Creates a temp file to be used for DeleteLine();
	 * @return File
	 */
	private static File createTempFile() {
		File newFile = new File("temp.txt");
		try {
			newFile.createNewFile();
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		return newFile;
	}
	/**
	 * Deletes the old file and renames the new file to the name of the old file.
	 * Helper method for deleteLine();
	 * @param file
	 * @param newFile
	 * @throws IOException
	 */
	private static void onCompletedDelete(File file, File newFile) throws IOException {
		file.delete();
		newFile.renameTo(file);
		
	}
}
