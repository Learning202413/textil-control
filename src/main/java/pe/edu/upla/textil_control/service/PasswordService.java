package pe.edu.upla.textil_control.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    // Método para encriptar la contraseña antes de guardarla en BD
    public String encriptar(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    // Método para verificar la contraseña en el Login
    public boolean verificar(String passwordPlana, String hashAlmacenado) {
        return BCrypt.checkpw(passwordPlana, hashAlmacenado);
    }
}