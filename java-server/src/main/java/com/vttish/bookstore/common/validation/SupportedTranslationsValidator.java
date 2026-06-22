package com.vttish.bookstore.common.validation;

import com.vttish.bookstore.common.config.LocalizationProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
public class SupportedTranslationsValidator
        implements ConstraintValidator<SupportedTranslations, Map<String, Object>> {

    private final LocalizationProperties localizationProps;
    private final MessageSource messageSource;

    @Override
    public boolean isValid(Map<String, Object> translations, ConstraintValidatorContext context) {
        if (translations == null || translations.isEmpty()) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        Locale locale = LocaleContextHolder.getLocale();
        if (!localizationProps.supportedLanguages().equals(translations.keySet())) {
            String message = messageSource.getMessage(
                    "error.validation.supported_translations",
                    new Object[] { localizationProps.supportedLanguages() },
                    locale
            );

            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        return true;
    }
}
