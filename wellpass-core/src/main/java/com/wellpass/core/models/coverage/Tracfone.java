package com.wellpass.core.models.coverage;

import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isNotFalse;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import java.util.Date;
import java.util.Objects;

/**
 * For capturing the membership info coming from Tracfone ingest file
 */
public class Tracfone {

  public String number;

  // not used
  public Boolean handsetValidationReq;

  public Boolean consent;

  public Boolean activated;

  public Boolean deleted;

  public Boolean safelinkActive;

  // not used
  public Boolean planActive;

  public Date lastModified;

  public boolean isValid() {
    return number != null
      && isNotFalse(activated) //must be nullOrTrue
      && isNotTrue(deleted) //must be nullOrFalse
      && isTrue(safelinkActive); //must be true
  }

  /**
   * Un-enroll members/families from health message programs
   * when "Safelink" Active turns from YES to NO,
   * or "Delete Phone"? turns from blank to YES.
   */
  public boolean isInactive() {
    return isTrue(deleted)
      || isFalse(safelinkActive);
  }

  public boolean isExactSame(Tracfone that) {
    if (that == null) {
      return false;
    }
    return Objects.equals(this.number, that.number)
      && Objects.equals(this.handsetValidationReq, that.handsetValidationReq)
      && Objects.equals(this.consent, that.consent)
      && Objects.equals(this.activated, that.activated)
      && Objects.equals(this.deleted, that.deleted)
      && Objects.equals(this.safelinkActive, that.safelinkActive)
      && Objects.equals(this.planActive, that.planActive);
  }

}
