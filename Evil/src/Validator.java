import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class Validator {
	public static String getString(Scanner sc, String prompt) {
		System.out.print(prompt);
		String s = sc.nextLine();
		return s;
	}

	public static double getDouble(Scanner sc, String prompt) {
		double d = 0.0;
		boolean isValid = false;
		while (isValid == false) {
			System.out.print(prompt);
			if (sc.hasNextDouble()) {
				d = sc.nextDouble();
				isValid = true;
			} else {
				System.out.println("Error! Invalid amount. Try again.");
			}
			sc.nextLine();
		}
		return d;
	}
	
	public static double getDouble(Scanner sc, String prompt, int min) {
		double d = 0;
		boolean isValid = false;
		while (isValid == false) {
			d = getDouble(sc, prompt);
			if (d < min) {
				System.out.println("Error! Amount must be greater than " + min + ".");
			} else {
				isValid = true;
			}
		}
		return d;
	}
	
	public static String getType(Scanner sc, String prompt) {
		String type = "";
		boolean isValid = false;
		
		while (isValid == false) {
			System.out.print(prompt);
			if (sc.hasNextLine()) {
				type = sc.nextLine();
				if (type.length() < 3) {
					if (type.toUpperCase().charAt(0) == 'A' ||type.toUpperCase().charAt(0) == 'T' || type.toUpperCase().charAt(0) == 'C' || type.toUpperCase().charAt(0) == 'D' || type.toUpperCase().charAt(0) == 'R' || type.toUpperCase().charAt(0) == 'W' || type.equals("-1")) {
						isValid = true;
					} else {
						isValid = false;
						System.out.println("Error! Invalid transaction type. Try again.");
					}
				} else {
					if (type.substring(0, 3).equalsIgnoreCase("DEP")) {
						isValid = true;
					} else {
						isValid = false;
						System.out.println("Error! Invalid transaction type. Try again.");
					}
				}
			}
		}
		return type;
	}
	
	public static Date getDate(Scanner sc, String prompt) {
		String inputDate = "";
		boolean isValid = false;
		
		SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
		Date date = new Date();
        while (isValid == false) {
        	System.out.print(prompt);
        	inputDate = sc.nextLine();
			try {
	            // Lenient conversion result in unexpected output.
	            date = sdf.parse(inputDate);
	            isValid = true;
	        } catch (ParseException e) {
	            isValid = false;
	            System.out.println("Error! The date format should be M/d/yyyy.");
	        }
        }
        return date;
	}
}