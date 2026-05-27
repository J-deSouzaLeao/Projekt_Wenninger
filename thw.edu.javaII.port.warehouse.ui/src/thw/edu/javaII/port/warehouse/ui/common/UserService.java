package thw.edu.javaII.port.warehouse.ui.common;

import java.util.ArrayList;
import java.util.List;

public class UserService {
	private final List<User> users;
	
	public UserService() {
		users = new ArrayList<>();
		users.add(new User("user","pass"));
		users.add(new User("jsh","jsh123"));
		users.add(new User("mmn","mmn123"));
	}
	
	public boolean checkLogin(String user, String pass) {
		for(User u : users) {
			if(u.userName().equals(user) && u.password().equals(pass)) {
				return true;
			}
		}
		return false;
	}

}

record User(String userName, String password) {


}
