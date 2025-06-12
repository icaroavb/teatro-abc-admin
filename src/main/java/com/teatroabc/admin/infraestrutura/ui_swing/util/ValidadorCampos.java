package com.teatroabc.admin.infraestrutura.ui_swing.util;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Classe utilitária para validação de campos de formulário.
 * Fornece métodos para validar formatos comuns como CPF, datas, e-mails, etc.
 */
public class ValidadorCampos {
    
    private static final Pattern PADRAO_EMAIL = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Valida um CPF.
     * @param cpf CPF a ser validado (pode conter pontos e traço)
     * @return true se o CPF for válido, false caso contrário
     */
    public static boolean validarCPF(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            return false;
        }
        
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");
        
        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }
        
        // Verifica se todos os dígitos são iguais
        boolean todosDigitosIguais = true;
        for (int i = 1; i < cpf.length(); i++) {
            if (cpf.charAt(i) != cpf.charAt(0)) {
                todosDigitosIguais = false;
                break;
            }
        }
        if (todosDigitosIguais) {
            return false;
        }
        
        // Calcula o primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += (cpf.charAt(i) - '0') * (10 - i);
        }
        int digito1 = 11 - (soma % 11);
        if (digito1 > 9) {
            digito1 = 0;
        }
        
        // Calcula o segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += (cpf.charAt(i) - '0') * (11 - i);
        }
        int digito2 = 11 - (soma % 11);
        if (digito2 > 9) {
            digito2 = 0;
        }
        
        // Verifica se os dígitos calculados são iguais aos dígitos do CPF
        return (cpf.charAt(9) - '0' == digito1) && (cpf.charAt(10) - '0' == digito2);
    }
    
    /**
     * Valida um endereço de e-mail.
     * @param email E-mail a ser validado
     * @return true se o e-mail for válido, false caso contrário
     */
    public static boolean validarEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        return PADRAO_EMAIL.matcher(email).matches();
    }
    
    /**
     * Valida uma data no formato dd/MM/yyyy.
     * @param data Data a ser validada
     * @return true se a data for válida, false caso contrário
     */
    public static boolean validarData(String data) {
        if (data == null || data.isEmpty()) {
            return false;
        }
        
        try {
            LocalDate.parse(data, FORMATO_DATA);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Valida um valor monetário.
     * @param valor Valor a ser validado
     * @return true se o valor for válido, false caso contrário
     */
    public static boolean validarValorMonetario(String valor) {
        if (valor == null || valor.isEmpty()) {
            return false;
        }
        
        // Remove caracteres monetários e substitui vírgula por ponto
        valor = valor.replaceAll("[R$\\s.]", "").replace(",", ".");
        
        try {
            double valorNumerico = Double.parseDouble(valor);
            return valorNumerico >= 0; // Valores negativos são inválidos
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Formata um CPF para exibição.
     * @param cpf CPF a ser formatado (apenas dígitos)
     * @return CPF formatado (XXX.XXX.XXX-XX)
     */
    public static String formatarCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        
        return cpf.substring(0, 3) + "." + 
               cpf.substring(3, 6) + "." + 
               cpf.substring(6, 9) + "-" + 
               cpf.substring(9, 11);
    }
    
    /**
     * Normaliza um CPF removendo todos os caracteres não numéricos.
     * @param cpf CPF a ser normalizado
     * @return CPF contendo apenas dígitos
     */
    public static String normalizarCPF(String cpf) {
        if (cpf == null) {
            return null;
        }
        
        return cpf.replaceAll("[^0-9]", "");
    }
}