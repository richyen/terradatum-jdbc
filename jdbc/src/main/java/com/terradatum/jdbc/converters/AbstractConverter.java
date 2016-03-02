package com.terradatum.jdbc.converters;

import com.google.common.base.Converter;
import com.google.common.reflect.TypeToken;

import java.util.Objects;

/**
 * @author rbellamy@terradatum.com
 * @date 2/2/16
 */
public abstract class AbstractConverter<A, B> extends Converter<A, B> {

  private final Class<? super A> forwardType = new TypeToken<A>(getClass()) {
  }.getRawType();
  private final Class<? super B> backwardType = new TypeToken<B>(getClass()) {
  }.getRawType();
  private final TypeToken<AbstractConverter<A, B>> typeToken = new TypeToken<AbstractConverter<A, B>>(getClass()) {
  };

  @Override
  public int hashCode() {
    return Objects.hashCode(this.typeToken);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof AbstractConverter) {
      AbstractConverter<?, ?> that = (AbstractConverter<?, ?>) object;
      return Objects.equals(this.typeToken, that.typeToken);
    }
    return false;
  }

  public Class<? super A> getForwardType() {
    return forwardType;
  }

  public Class<? super B> getBackwardType() {
    return backwardType;
  }
}
