package roundforest.model;

import com.google.common.base.Objects;

public class Review {
    public final String profileName;
    public final String productId;
    public final String review;

    public Review(String profileName, String productId, String review) {
        this.profileName = profileName;
        this.productId = productId;
        this.review = review;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review1 = (Review) o;
        return Objects.equal(profileName, review1.profileName) &&
                Objects.equal(productId, review1.productId) &&
                Objects.equal(review, review1.review);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(profileName, productId, review);
    }

    @Override
    public String toString() {
        return "Review{" +
                "profileName='" + profileName + '\'' +
                ", productId='" + productId + '\'' +
                ", review='" + review + '\'' +
                '}';
    }
}
