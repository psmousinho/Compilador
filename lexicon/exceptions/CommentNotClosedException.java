/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexico.exceptions;

/**
 *
 * @author Pablo Suria
 */
public class CommentNotClosedException extends Exception {

    public CommentNotClosedException() {
        super("Comentario Aberto e Não Fechado");
    }
    
}
