import java.io.*;
import java.util.Scanner;
public class TextBuddy {
	private static boolean isRunning = true;
	private static String txtFile;
	private static String cmd;
	private static BufferedReader reader,tempReader;
	private static BufferedWriter writer,tempWriter;
	private static Scanner sc;
	private static String[] Commands;
	private static final String MESSAGE_WELCOME ="Welcome to TextBuddy. %1$s is ready for use.";
	private static final String MESSAGE_NOFILE ="Since no input file has been specified,one has been created for you";

	public static void main(String[] args) throws IOException{
		
		File file = toBeEdited(args);
		sc = new Scanner(System.in);

		while(isRunning){
			Commands = getCommand(sc);
			parseCommand(Commands, file);
		}
		sc.close();
	}
	private static void parseCommand(String[] Commands, File 
			file)throws IOException, FileNotFoundException {
		String command = Commands[0].toLowerCase();
		if(command.equals("exit")){
			stopRunning();
			displayMessage("Goodbye");
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
	private static void addNewLine(String[] Commands, File file) throws IOException {
		String toBeAdded = "";
		for(int i=1;i<Commands.length;i++){
			toBeAdded=toBeAdded + Commands[i]+" ";
		}
		addLine(toBeAdded.trim(),file,file.getName());
	}
	private static void stopRunning() {
		isRunning = false;
	}
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
	private static boolean hasInput(String[] args) {
		return args[0]!=null;
	}
	private static String[] getCommand(Scanner sc) {
		displayCommand();
		cmd = sc.nextLine();
		Commands = cmd.split(" ");
		return Commands;
	}
	private static void displayCommand() {
		System.out.print("Command: ");
	}
	private static void displayMessage(String string1) {
		System.out.println(string1);
	}
	private static File accessFile(String args) throws IOException {
		File file = new File(args);
		if(!file.exists()){
			file.createNewFile();
		}
		return file;
	}
	private static void addLine(String toBeAdded,File file,String fileName)throws IOException{
	
		writer = createWriter(file);
		reader = createReader(file);
		if(getNextLine(reader) ==null){
			writer.write(toBeAdded);
		}else{
			writer.newLine();
			writer.write(toBeAdded);
		}
		
		System.out.println("added to "+ fileName+": \""+toBeAdded+"\"");
		closeStreams(writer, reader);
	}
	private static BufferedWriter createWriter(File file) throws IOException {
		return new BufferedWriter(new FileWriter(file.getAbsoluteFile(),true));
	}
	private static void closeStreams(BufferedWriter writer, BufferedReader reader)
			throws IOException {
		if(writer !=null){
			writer.flush();
			writer.close();
			writer=null;
		}
		if(reader!= null){
			reader.close();
			reader = null;
		}
	
	}
	private static void addLine(String toBeAdded,File file)throws IOException{
		tempWriter = createWriter(file);
		tempReader = createReader(file);
		if(getNextLine(tempReader) ==null){
			tempWriter.write(toBeAdded);
		}else{
			tempWriter.newLine();
			tempWriter.write(toBeAdded);
		}
		
		closeStreams(tempWriter, tempReader);
	}
	private static void clearAll(File file)throws IOException{
		File newFile = createTempFile();
		BufferedWriter writer = createWriter(file);
		onCompletedDelete(file,newFile);
		closeStreams(writer, null);
		displayMessage("all content deleted from "+file.getName());
	}
	private static void displayAll(File file) throws IOException{
		try {
			reader =createReader(file);
			String currentLine = getNextLine(reader);
			int count = 1;
			if(isEmpty(currentLine)){
				System.out.println(file.getName()+" is empty");
				closeStreams(null,reader);
				return ;
			}
			do{
				displayMessage(count++ +". "+currentLine);
				currentLine = getNextLine(reader);
			}while(currentLine != null);
			closeStreams(null,reader);
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	private static BufferedReader createReader(File file)
			throws FileNotFoundException {
		return new BufferedReader(new FileReader(file));
	}
	private static String getNextLine(BufferedReader reader) throws IOException {
		return reader.readLine();
	}
	private static boolean isEmpty(String currentLine) {
		return currentLine==null;
	}
	private static void deleteLine(File file,int toBeDeleted)throws IOException{
		File newFile = createTempFile();
		writer = createWriter(file);
		reader =createReader(file);
		
		
		String line;
		int count = 1;
		try {
			while((line =reader.readLine())!= null){			
				if(count!= toBeDeleted){
					addLine(line,newFile);
				}else{
					displayMessage("deleted from "+ file.getName()+": "+line);
				}
				count++;
			}
			
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		closeStreams(writer,reader);
		file.delete();
		newFile.renameTo(file);
	}
	private static File createTempFile() {
		File newFile = new File("temp.txt");
		try {
			newFile.createNewFile();
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		return newFile;
	}
	private static void onCompletedDelete(File file, File newFile) throws IOException {
		file.delete();
		newFile.renameTo(file);
		
	}
}
