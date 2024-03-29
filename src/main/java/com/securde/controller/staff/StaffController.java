package com.securde.controller.staff;

import com.securde.controller.RoomController;
import com.securde.model.reservable.Room;
import com.securde.model.reservable.Text;
import com.securde.model.reservation.RoomReservation;
import com.securde.service.ReservableService;
import com.securde.service.ReservationService;
import com.securde.service.UserService;
import com.securde.validator.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import sun.security.util.Password;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by kevin on 6/26/2017.
 */

@Controller
public class StaffController {

    @Autowired
    ReservableService reservableService;

    @Autowired
    ReservationService reservationService;

    @Autowired
    UserService userService;

    private static Logger logger = LoggerFactory.getLogger(StaffController.class);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar calendar = Calendar.getInstance();

    @RequestMapping(value = {"/staff", "/staff/home"}, method = RequestMethod.GET)
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("staff/home2");
        return modelAndView;
    }

    @RequestMapping(value = {"/staff/text"}, params = "!id", method = RequestMethod.GET)
    public ModelAndView viewManageTexts() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("texts", reservableService.getAllTexts());
        modelAndView.setViewName("staff/text_management");
        return modelAndView;
    }

    @RequestMapping(value = {"/staff/text"}, method = RequestMethod.GET)
    public ModelAndView viewTextDetails(@RequestParam("id") Integer id) {
        ModelAndView modelAndView = new ModelAndView();

        Text text = reservableService.getText(id);

        modelAndView.addObject("text", text);
        modelAndView.setViewName("staff/text_details");

        return modelAndView;
    }

    @RequestMapping(value = {"/staff/text/add"}, method = RequestMethod.GET)
    public ModelAndView viewAddText() {
        ModelAndView modelAndView = new ModelAndView();

        ArrayList<String> tags = new ArrayList<>();

        while(tags.size() < 5) {
            tags.add("");
        }

        Text text = new Text()
                .setTags(tags);

        modelAndView.addObject("text", text);
        modelAndView.setViewName("staff/add_text");

        return modelAndView;
    }

    @RequestMapping(value = {"/staff/text/add"}, method = RequestMethod.POST)
    public ModelAndView addText(@Valid Text text, BindingResult bindingResult, @RequestParam("radio-type") String type,
                                Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("staff/add_text");
        }
        else {
            text.setType(type).setStatus(Text.Status.AVAILABLE);
            reservableService.saveText(text);

            org.springframework.security.core.userdetails.User authUser = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            logger.info(authUser.getUsername() + " added a new " + text.getType());

            modelAndView.setViewName("redirect:/staff/text");
        }

        return modelAndView;
    }

    @RequestMapping(value = {"staff/text"}, method = RequestMethod.PUT)
    public ModelAndView editTextDetails(@Valid Text text, BindingResult bindingResult, @RequestParam("radio-type") String type,
                                        Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("error", true);
            modelAndView.setViewName("staff/text_details");
        }
        else {
            text.setType(type);
            reservableService.saveText(text);

            org.springframework.security.core.userdetails.User authUser = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            logger.info(authUser.getUsername() + " updated text with ID " + text.getTextId());

            modelAndView.setViewName("redirect:/staff/text?id=" + text.getTextId());
        }

        return modelAndView;
    }

    @RequestMapping(value = {"staff/text"}, method = RequestMethod.DELETE)
    public RedirectView deleteText(@RequestParam("text-id") Integer id,
                                   Authentication authentication) {
        reservableService.deleteText(id);

        org.springframework.security.core.userdetails.User authUser = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        logger.info(authUser.getUsername() + " deleted text with ID " + id);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/staff/text");

        return redirectView;
    }

    @RequestMapping(value = {"staff/rooms"}, method = RequestMethod.GET)
    public ModelAndView viewRooms(@RequestParam(value = "date", required = false) String inputDate) {
        ModelAndView modelAndView = new ModelAndView();

        List<Room> rooms = reservableService.getAllRooms();

        if (inputDate == null) {
            Date date = new Date();
            inputDate = sdf.format(date);
        }

        ArrayList<RoomReservation> reservedSlots = reservationService.getRoomReservationsByDate(inputDate);

        List<RoomReservation.RoomIDAndStartTime> roomIDAndStartTimes = new ArrayList<>();

        for (int i = 0; i < reservedSlots.size(); i++) {
            RoomReservation reservedSlot = reservedSlots.get(i);
            roomIDAndStartTimes.add(new RoomReservation.RoomIDAndStartTime(reservedSlot.getRoom().getRoomId(), reservedSlot.getReservationStartTime()));
        }

        modelAndView.setViewName("staff/rooms");
        modelAndView.addObject("rooms", rooms);
        modelAndView.addObject("times", RoomController.getTimes());
        modelAndView.addObject("reserved_slots", roomIDAndStartTimes);
        modelAndView.addObject("inputDate", inputDate);

        return modelAndView;
    }

    @RequestMapping(value = {"staff/change_password"}, method = RequestMethod.GET)
    public ModelAndView viewChangePassword() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("staff/change_password");
        return modelAndView;
    }

    @RequestMapping(value = {"staff/change_password"}, method = RequestMethod.POST)
    public ModelAndView changePassword(Authentication authentication,
                                       @RequestParam("currentPassword") String currentPassword,
                                       @RequestParam("newPassword") String newPassword,
                                       @RequestParam("confirmNewPassword") String confirmNewPassword) {

        ModelAndView modelAndView = new ModelAndView();
        User authUser = (User) authentication.getPrincipal();
        boolean hasError;
        PasswordValidator passwordValidator = new PasswordValidator();

        hasError = !userService.validateUser(authUser.getUsername(), currentPassword);
        hasError = hasError || !newPassword.equals(confirmNewPassword);
        hasError = hasError || !passwordValidator.isValidPassword(currentPassword) ||
                !passwordValidator.isValidPassword(newPassword);

        if (hasError) {
            modelAndView.addObject("errorMessage", "Invalid input. Try again.");
        }
        else {
            userService.changePassword(authUser.getUsername(), newPassword);
            logger.info(authUser.getUsername() + " changed password");
            modelAndView.addObject("successMessage", "Successfully changed password.");
        }

        modelAndView.setViewName("staff/change_password");

        return modelAndView;
    }
}
