/**
 * Copyright (C) 2017 Red Hat, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atlasmap.xml.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.atlasmap.api.AtlasConversionService;
import io.atlasmap.core.BaseModuleValidationService;
import io.atlasmap.spi.AtlasModuleDetail;
import io.atlasmap.spi.AtlasValidator;
import io.atlasmap.v2.Field;
import io.atlasmap.v2.Validation;
import io.atlasmap.v2.ValidationScope;
import io.atlasmap.v2.ValidationStatus;
import io.atlasmap.validators.NonNullValidator;
import io.atlasmap.xml.v2.XmlField;

public class XmlValidationService extends BaseModuleValidationService<XmlField> {

    private static Map<String, AtlasValidator> validatorMap = new HashMap<>();
    private AtlasModuleDetail moduleDetail = XmlModule.class.getAnnotation(AtlasModuleDetail.class);

    public XmlValidationService(AtlasConversionService conversionService) {
        super(conversionService);
        init();
    }

    public void init() {
        NonNullValidator javaFileNameNonNullValidator = new NonNullValidator(ValidationScope.MAPPING,
                "The name element must not be null nor empty");
        NonNullValidator javaFilePathNonNullValidator = new NonNullValidator(ValidationScope.MAPPING,
                "The path element must not be null nor empty");
        NonNullValidator inputFieldTypeNonNullValidator = new NonNullValidator(ValidationScope.MAPPING,
                "Field type should not be null nor empty");
        NonNullValidator outputFieldTypeNonNullValidator = new NonNullValidator(ValidationScope.MAPPING,
                "Field type should not be null nor empty");
        NonNullValidator fieldTypeNonNullValidator = new NonNullValidator(ValidationScope.MAPPING,
                "Filed type should not be null nor empty");

        validatorMap.put("xml.field.type.not.null", fieldTypeNonNullValidator);
        validatorMap.put("xml.field.name.not.null", javaFileNameNonNullValidator);
        validatorMap.put("xml.field.path.not.null", javaFilePathNonNullValidator);
        validatorMap.put("input.field.type.not.null", inputFieldTypeNonNullValidator);
        validatorMap.put("output.field.type.not.null", outputFieldTypeNonNullValidator);
    }

    public void destroy() {
        validatorMap.clear();
    }

    @Override
    protected AtlasModuleDetail getModuleDetail() {
        return moduleDetail;
    }

    @Override
    protected Class<XmlField> getFieldType() {
        return XmlField.class;
    }

    @Override
    protected String getModuleFieldName(XmlField field) {
        return field.getName() != null ? field.getName() : field.getPath();
    }

    @Override
    protected void validateModuleField(String mappingId, Field field, FieldDirection direction, List<Validation> validations) {
        // TODO check that it is a valid type on the AtlasContext

        validatorMap.get("xml.field.type.not.null").validate(field, validations, mappingId, ValidationStatus.WARN);
        if (direction == FieldDirection.INPUT) {
            if (field != null) {
                validatorMap.get("input.field.type.not.null").validate(field.getFieldType(), validations,
                        mappingId, ValidationStatus.WARN);
            }
        } else {
            if (field != null) {
                validatorMap.get("output.field.type.not.null").validate(field.getFieldType(), validations,
                        mappingId, ValidationStatus.WARN);
            }
        }
        if (field != null && field instanceof XmlField) {
        	
            if ((((XmlField)field).getName() == null && field.getPath() == null)) {
                Validation validation = new Validation();
                validation.setScope(ValidationScope.MAPPING);
                validation.setId(mappingId);
                validation.setMessage("One of path or name must be specified");
                validation.setStatus(ValidationStatus.ERROR);
                validations.add(validation);
            } else if (((XmlField)field).getName() != null && field.getPath() == null) {
                validatorMap.get("xml.field.name.not.null").validate(((XmlField)field).getName(), validations, mappingId);
            } else if (((XmlField)field).getName() == null && field.getPath() != null) {
                validatorMap.get("xml.field.path.not.null").validate(field.getPath(), validations, mappingId);
            }
        }
    }
}
