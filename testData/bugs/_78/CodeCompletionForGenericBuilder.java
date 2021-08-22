/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class DemoMapper {

    // code completion for target broken - see comments down below to see the culprit
    @Mapping(target = "<caret>", source = "input")
    public abstract DemoTypeWithBuilder map(String input);

    public static class DemoTypeWithBuilder {

        protected String status;
        protected int data;

        public String getStatus() {
            return status;
        }

        public void setStatus(String value) {
            this.status = value;
        }

        public int getData() {
            return data;
        }

        public void setData(int value) {
            this.data = value;
        }

        /**
         * when commenting out this static builder method, then auto complete lists values as expected:
         * "status", "data"
         */
        public static DemoTypeWithBuilder.Builder<Void> builder() {
            return new DemoTypeWithBuilder.Builder<Void>( null, null, false );
        }

        public static class Builder<_B> {

            protected final _B _parentBuilder;
            protected final DemoTypeWithBuilder _storedValue;
            private String status;
            private int data;

            public Builder(final _B _parentBuilder, final DemoTypeWithBuilder _other, final boolean _copy) {
                this._parentBuilder = _parentBuilder;
                if ( _other != null ) {
                    if ( _copy ) {
                        _storedValue = null;
                        this.status = _other.status;
                        this.data = _other.data;
                    }
                    else {
                        _storedValue = _other;
                    }
                }
                else {
                    _storedValue = null;
                }
            }

            /**
             * when commenting out this constructor, then auto-complete lists:
             * "_copy", "_other", "_parentBuilder"
             */
            public Builder(final _B _parentBuilder, final DemoTypeWithBuilder _other, final boolean _copy,
                           final PropertyTree _propertyTree, final
                           PropertyTreeUse _propertyTreeUse) {
                this._parentBuilder = _parentBuilder;
                if ( _other != null ) {
                    if ( _copy ) {
                        _storedValue = null;
                    }
                    else {
                        _storedValue = _other;
                    }
                }
                else {
                    _storedValue = null;
                }
            }

            protected <_P extends DemoTypeWithBuilder> _P init(final _P _product) {
                _product.status = this.status;
                _product.data = this.data;
                return _product;
            }

            public DemoTypeWithBuilder.Builder<_B> withStatus(final String status) {
                this.status = status;
                return this;
            }

            public DemoTypeWithBuilder.Builder<_B> withData(final int data) {
                this.data = data;
                return this;
            }

            public DemoTypeWithBuilder build() {
                if ( _storedValue == null ) {
                    return this.init( new DemoTypeWithBuilder() );
                }
                else {
                    return _storedValue;
                }
            }
        }
    }

    // mock for com.kscs.util.jaxb.PropertyTreeUse
    public static class PropertyTreeUse {

    }

    // mock for com.kscs.util.jaxb.PropertyTree
    public static class PropertyTree {

    }
}
