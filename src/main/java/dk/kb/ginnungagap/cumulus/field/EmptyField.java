package dk.kb.ginnungagap.cumulus.field;

import com.canto.cumulus.FieldDefinition;

public class EmptyField extends Field {

    public EmptyField(FieldDefinition fieldDefinition, String fieldType) {
        super(fieldDefinition, fieldType);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

}
