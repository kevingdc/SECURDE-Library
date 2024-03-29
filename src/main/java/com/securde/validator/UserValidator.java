package com.securde.validator;

import com.securde.model.account.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by kevin on 6/27/2017.
 */

@Component
public class UserValidator implements Validator {

    private static final String ERROR_CODE_REQUIRED = "field.required";
    private static final String ERROR_LENGTH = "field.length";
    private static final String ERROR_CODE_MIN_LENGTH = "field.min.length";
    private static final String ERROR_CODE_MAX_LENGTH = "field.max.length";

    private static final int USERNAME_MIN_LENGTH = 4;
    private static final int USERNAME_MAX_LENGTH = 16;

    private static final int PASSWORD_MIN_LENGTH = 6;
    private static final int PASSWORD_MAX_LENGTH = 32;

    private static final int ID_NUMBER_LENGTH = 8;

    private static final int EMAIL_MAX_LENGTH = 64;

    private static final int NAME_MAX_LENGTH = 64;
    private static final int MIDDLE_INITIAL_MAX_LENGTH = 5;

    private static final int SECRET_QUESTION_MAX_LENGTH = 64;
    private static final int SECRET_ANSWER_MAX_LENGTH = 32;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        validateUsername(user.getUsername(), errors);
        validatePassword(user.getPassword(), errors);

        if (user.isRegularUser()) {
            validateIdNumber(user.getIdNumber(), errors);
            validateEmail(user.getEmail(), errors);
            validateFirstName(user.getFirstName(), errors);
            validateMiddleInitial(user.getMiddleInitial(), errors);
            validateLastName(user.getLastName(), errors);
            validateBirthday(user.getBirthday(), errors);
            validateSecretQuestion(user.getSecretQuestion(), errors);
            validateSecretAnswer(user.getSecretAnswer(), errors);
        }
    }


    private void validateUsername(String username, Errors errors) {
        if (isNullOrEmpty(username)) {
            rejectRequired(errors, "username");
        }
        else if (username.length() < USERNAME_MIN_LENGTH) {
            errors.rejectValue("username", ERROR_CODE_MIN_LENGTH, "Username must be at least " +
                    USERNAME_MIN_LENGTH + " characters.");
        }
        else if (username.length() > USERNAME_MAX_LENGTH) {
            errors.rejectValue("username", ERROR_CODE_MAX_LENGTH, "Username cannot exceed " +
                    USERNAME_MAX_LENGTH + " characters.");
        }
    }

    private void validatePassword(String password, Errors errors) {
        if (isNullOrEmpty(password)) {
            rejectRequired(errors, "password");
        }
        else if (password.length() < PASSWORD_MIN_LENGTH) {
            errors.rejectValue("password", ERROR_CODE_MIN_LENGTH, "Password must be at least " +
                    PASSWORD_MIN_LENGTH + " characters.");
        }
        else if (password.length() > PASSWORD_MAX_LENGTH) {
            errors.rejectValue("username", ERROR_CODE_MAX_LENGTH, "Password cannot exceed " +
                    PASSWORD_MAX_LENGTH + " characters.");
        }
    }

    private void validateIdNumber(String idNumber, Errors errors) {
        if (isNullOrEmpty(idNumber)) {
//            rejectRequired(errors, "idNumber");
            errors.rejectValue("idNumber", ERROR_CODE_REQUIRED, "Please input a valid ID number.");
        }

        else if (idNumber.length() != ID_NUMBER_LENGTH) {
            errors.rejectValue("idNumber", ERROR_LENGTH, "ID Number must be exactly " + ID_NUMBER_LENGTH + " characters.");
        }
    }

    private void validateEmail(String email, Errors errors) {
        if (isNullOrEmpty(email)) {
            rejectRequired(errors, "email");
        }

        else if (email.length() > EMAIL_MAX_LENGTH) {
            errors.rejectValue("email", ERROR_CODE_MAX_LENGTH, "Your email address is too long!");
        }
    }

    private void validateFirstName(String firstName, Errors errors) {
        if (isNullOrEmpty(firstName)) {
//            rejectRequired(errors, "firstName");
            errors.rejectValue("firstName", ERROR_CODE_REQUIRED, "Please input a valid first name.");

        }
        else if(firstName.length() > NAME_MAX_LENGTH) {
            errors.rejectValue("firstName", ERROR_CODE_MAX_LENGTH, "Your first name is too long!");
        }
    }

    private void validateMiddleInitial(String middleInitial, Errors errors) {
        if (isNullOrEmpty(middleInitial)) {
//            rejectRequired(errors, "middleInitial");
            errors.rejectValue("middleInitial", ERROR_CODE_REQUIRED, "Please input a valid middle initial.");
        }
        else if (middleInitial.length() > MIDDLE_INITIAL_MAX_LENGTH) {
            errors.rejectValue("middleInitial", ERROR_CODE_MAX_LENGTH, "Your middle initial is too long!");
        }
    }

    private void validateLastName(String lastName, Errors errors) {
        if (isNullOrEmpty(lastName)) {
//            rejectRequired(errors, "lastName");
            errors.rejectValue("lastName", ERROR_CODE_REQUIRED, "Please input a valid last name.");
        }
        else if(lastName.length() > NAME_MAX_LENGTH) {
            errors.rejectValue("lastName", ERROR_CODE_MAX_LENGTH, "Your last name is too long!");
        }
    }

    private void validateBirthday(String birthday, Errors errors) {
        if (isNullOrEmpty(birthday)) {
            errors.rejectValue("birthday", ERROR_CODE_REQUIRED, "Please input a valid birthday.");
        }
    }

    private void validateSecretQuestion(String secretQuestion, Errors errors) {
        if (isNullOrEmpty(secretQuestion)) {
//            rejectRequired(errors, "secretQuestion");
            errors.rejectValue("secretQuestion", ERROR_CODE_REQUIRED, "Please input a valid secret question.");
        }
        else if (secretQuestion.length() > SECRET_QUESTION_MAX_LENGTH) {
            errors.rejectValue("secretQuestion", ERROR_CODE_MAX_LENGTH, "Your secret question is too long!");
        }
    }

    private void validateSecretAnswer(String secretAnswer, Errors errors) {
        if (isNullOrEmpty(secretAnswer)) {
//            rejectRequired(errors, "secretAnswer");
            errors.rejectValue("secretAnswer", ERROR_CODE_REQUIRED, "Please input a valid secret answer.");
        }
        else if (secretAnswer.length() > SECRET_ANSWER_MAX_LENGTH) {
            errors.rejectValue("secretAnswer", ERROR_CODE_MAX_LENGTH, "Your secret answer is too long!");
        }
    }

    private boolean isNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }

    private void rejectRequired(Errors errors, String field) {
        errors.rejectValue(field, ERROR_CODE_REQUIRED, "Please input a valid " + field + ".");
    }
}
