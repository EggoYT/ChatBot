package Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

	static String companyId = "someCompany";
	static String userId = "someUser";
	static BufferedReader c_input;
	static BufferedWriter c_output;
	static Scanner input;
	
	public static void checkRequest(String request, int deep) throws IOException {
		System.out.println("Запрос принят. Ожидайте ответа...");
		c_output.write(companyId + "\n");
		c_output.write(userId + "\n");
		c_output.write(Long.toString(new Timestamp(System.currentTimeMillis()).getTime()) + "\n");
		c_output.write(request + "\n");
		c_output.flush();
		
			int is_good = Integer.parseInt(c_input.readLine());
			String answer = c_input.readLine();
			while(c_input.ready())
				c_input.read();
			try {
				if(is_good == 0) {
					System.out.println(answer);
					
					if(deep < 1) {
						System.out.println("Желаете уточнить запрос?");
						String user_answer = input.nextLine();
						c_output.write(user_answer + "\n");
						c_output.flush();
						boolean is = Boolean.parseBoolean(c_input.readLine());
						if(is) {
							System.out.println("Напишите уточняющий запрос:");
							user_answer = input.nextLine();
							checkRequest(user_answer, deep + 1);
						}
					}
					
					if(deep == 0) {
						System.out.println("Оцените работу бота от 1 до 5");
						try {
							int v = Math.min(Math.max(input.nextInt(), 1), 5);
							c_output.write(v + "\n");
							c_output.flush();
							System.out.println("Спасибо за оценку!");
						} catch(InputMismatchException e) {
							throw new Exception("требовалось число от 1 до 5");
						}
					}
				} else {
					throw new Exception(answer);
				}
			} catch(Exception e) {
				System.out.println("Ошибка обработки запроса:" + e.getMessage());
			}
	}
	
	public static void main(String[] args) {
		try(Socket socket = new Socket("localhost", 5566)) {
			input = new Scanner(System.in);
			c_input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			c_output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			System.out.println("Бот подключен к серверу!");
			
			while(true) {
				try {
					//System.out.println("Введите запрос через комадну /запрос");
					String msg = input.nextLine();
					if(msg.length() > 0)
						if(msg.indexOf("/запрос ") == 0)
							checkRequest(msg.substring(7), 0);
						else if(msg.charAt(0) == '/')
							System.out.println("Неизвестная команда. Введите /запрос для отправки запроса");
				} catch(Exception e) {
					System.err.println(e.getMessage());
					System.out.println("Соединение разорвано!");
					break;
				}
			}
		} catch (IOException e) {
			System.err.println("Can't connect to the server!");
		}
	}

}
