package com.mercado.sistema.general;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class MetodosGenericos {
    public static void setModelAttributeErro(BindingResult result, Model model, String modelo) {
        String campoError = "";
        for (FieldError erro : result.getFieldErrors()) {
          campoError +=
              "Campo com erro: "
                  + erro.getField().toUpperCase()
                  + "\\n\n "
                  + capitalize(erro.getDefaultMessage())
                  + "\\n\n";
        }
        String mensagemErro = "Erro ao salvar " + modelo + ":\\n\n" + campoError;
        model.addAttribute(
                "erro", mensagemErro + "\\n  ATENÇÃO não foi salvo!");
    }
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
