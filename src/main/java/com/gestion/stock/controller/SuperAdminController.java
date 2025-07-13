package com.gestion.stock.controller;

import com.gestion.stock.entities.Erole;
import com.gestion.stock.entities.Role;
import com.gestion.stock.entities.User;
import com.gestion.stock.repository.RoleRepository;
import com.gestion.stock.service.EmailService;
import com.gestion.stock.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller("/superadmin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;


    @GetMapping("/list_Users")
    public String getAllUsers(Model model) {
        List<User> users =
                userService.findAllUsers();

        model.addAttribute("users", users);
        return "superadmin/listUsers";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        return "superadmin/addUser";
    }

    @GetMapping("/users/add")
    public String addUser(@Valid @ModelAttribute("user") User userDto,
                          Model model,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          @RequestParam("rolename") Erole rolename) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            return "superadmin/addUser";
        }
        try {
            User createdUser =
                    userService.registerNewUserAccount(userDto, rolename);
            redirectAttributes.addFlashAttribute("succes", "User" + createdUser.getUserName() + "added successfully");
            return "redirect:/superadmin/listUsers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "ereur" + e.getMessage());
            return "redirect:/superadmin/listUsers";
        }

    }

    @GetMapping("/edit-user/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {

        Optional<User> optionalUser =
                userService.findUserById(id);
        if (optionalUser.isEmpty()) {
            return "redirect:/superadmin/listUsers";
        }
        User user = optionalUser.get();
        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepository.findAll());

        Set<Erole> userRoleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());

        model.addAttribute("userRoleNames", userRoleNames);
        return "superadmin/editUser";

    }

    @PostMapping("/edit/{id}")
    public String editUser(@Valid @ModelAttribute("userDto") User userDto,
                           @PathVariable("id") Long id,
                           @RequestParam("roleName") Erole roleName,
                           RedirectAttributes redirectAttributes,
                           Model model,
                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            return "superadmin/editUser";
        }
        try {
            User updatedUser = userService.updateUser(id, userDto, roleName);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Mise à jour réussie pour l'utilisateur : " + updatedUser.getUserName());
        } catch (Exception e) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("errorMessage", e.getMessage());
            return "superadmin/editUser";
        }
        return "redirect:/superadmin/listUsers";

    }

    @GetMapping("/update-role")
    public String updateUserRole(@RequestParam("id") Long id,
                                 @RequestParam("newRole") Erole newRole,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.updateRole(id, newRole);
            redirectAttributes.addFlashAttribute("message", "Role à jour avec succes");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/superadmin/listUsers";
    }

    @GetMapping("/enable-user/{id}")
    public String enableUser(@PathVariable("id") Long id,
                             RedirectAttributes redirectAttributes) {

        try {
            userService.enableUserAccount(id);
            redirectAttributes.addFlashAttribute("succes", "User activé avec succes");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/superadmin/listUsers";
    }

    @GetMapping("/disable-user/{id}")
    public String disableUser(@PathVariable("id") Long id,
                              RedirectAttributes redirectAttributes) {
        try {
            userService.disableUserAccount(id);
            redirectAttributes.addFlashAttribute("succes", "User desactivé avec succes");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/superadmin/listUsers";
    }

    @GetMapping("/delete-user")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("succes", "User a ete supprime avec succes");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/superadmin/listUsers";
    }


}
