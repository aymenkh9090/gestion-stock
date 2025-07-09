package com.gestion.stock.controller;

import com.gestion.stock.entities.Erole;
import com.gestion.stock.entities.Role;
import com.gestion.stock.entities.User;
import com.gestion.stock.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;



@Controller
@AllArgsConstructor
public class AuthController {

     private  UserService userService;


    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(){
        return "home";
    }

    //Affiche page login

    @GetMapping("/login")
    public String login(@RequestParam(value = "error",required = false) String erreur,
                        @RequestParam(value = "logout", required = false) String logout,
                        @RequestParam(value="expired" , required = false ) String expired,
                        Model model
    ){
        if(erreur != null){
            model.addAttribute("erreur" , "Nom d'utilisateur nom valide !!");
        }
        if(logout != null){
            model.addAttribute("logout" , "Vous avez Deconnecté avec succés");
        }
        if (expired != null){
            model.addAttribute("expired" , "Votre session a eté expiré!! Veuillez se reconnectez");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model mode){
        mode.addAttribute("user", new User());
        return "register";
    }

   /* @PostMapping("/register/save")
    public String processRegistration(@Valid User user,
                                      BindingResult bindingResult,
                                      Model model,
                                      Set<Erole> roles,
                                      @RequestParam String confirmedPaassword,
                                      RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            model.addAttribute("erreur" , "Erreur !!");
            return "register";
        }
        if(!confirmedPaassword.equals(user.getPassword()) ){
            redirectAttributes.addFlashAttribute("erreur" , "les mots de pass ne sont pas conformes !!");
            return "register";
        }

        return "redirect:/login";
    }
*/

    @PostMapping("/register/save")
    public String processRegistration(@ModelAttribute User user,
                                     @RequestParam ("confirmedPassword")String confirmedPassword,
                                      RedirectAttributes redirectAttributes){

        if(!user.getPassword().equals(confirmedPassword)){
            redirectAttributes.addFlashAttribute("error", "Les mots de passe ne correspondent pas");
            return "redirect:/register";
        }
        try{
            userService.registerNewUserAccount(user,Erole.ROLE_USER);
            return "redirect:/login";

       }catch (Exception e){
          redirectAttributes.addFlashAttribute("error", e.getMessage());
           return "redirect:/register";
       }
    }

    @GetMapping("/activate")
    public String activateAccount(@RequestParam String token, Model model) {
        boolean activated = userService.activateUserAccount(token);

        if (activated) {
            model.addAttribute("success", true);
            return "activation";
        } else {
            model.addAttribute("error", "Token d'activation invalide ou expiré");
            return "activation";
        }
    }











}
