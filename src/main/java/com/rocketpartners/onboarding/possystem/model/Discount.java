package com.rocketpartners.onboarding.possystem.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Represents a discount that can be applied to a transaction. This model is designed to handle the following types
 * of discounts:
 * <p>
 * 1. Percentage-based: Discount given as percentage off original price
 * 2. Value-based: Discount given as fixed monetary amount off original price
 * 3. "Buy X Get Y Free"-based: Discount given where buying a certain number of items (X) qualifies for additional
 * items (Y) for free
 * 4. "Buy X Get Y At Z Off"-based: Discount where buying a certain number of items (X) qualifies for an additional
 * set of items (Y) at percentage (Z) off
 * <p>
 * <p>
 * See here for more details of each discount type:
 * <u1>
 * <li>Percentage-based: Discount given as percentage off original price</li>
 *     <ul>
 *         <li>Example: "Get 50% off"</li>
 *         <li>Attributes:</li>
 *         <ul>
 *             <li>Type: "percentage"</li>
 *             <li>Value: The percentage off (e.g., 50 for 50% off)</li>
 *             <li>Minimum Quantity: Optional, to specify a minimum purchase requirement; defaults to 1</li>
 *             <li>Discounted quantity: Not applicable</li>
 *             <li>Value-based: Discount given as fixed monetary amount off original price</li>
 *             <li>Example: "Get $1 off"</li>
 *             <li>Attributes:</li>
 *         </ul>
 *     </ul>
 * <li>Value-based: Discount given as fixed monetary amount off original price</li>
 *    <ul>
 *        <li>Example: "Get $1 off"</li>
 *        <li>Attributes:</li>
 *        <ul>
 *            <li>Type: "value"</li>
 *            <li>Value: The amount off (e.g., 1 for $1 off)</li>
 *            <li>Minimum Quantity: Optional, to specify a minimum purchase requirement; defaults to 1</li>
 *            <li>Discounted quantity: Not applicable</li>
 *            <li>"Buy X Get Y Free"-based: Discount given where buying a certain number of items (X) qualifies for
 *            additional items (Y) for free</li>
 *            <li>Example: "Buy 2, get 1 free"</li>
 *        </ul>
 *    </ul>
 * <li>"Buy X Get Y Free"-based: Discount given where buying a certain number of items (X) qualifies for additional
 * items (Y) for free</li>
 *   <ul>
 *       <li>Example: "Buy 2, get 1 free"</li>
 *       <li>Attributes:</li>
 *       <ul>
 *           <li>Type: "buyXgetY"</li>
 *           <li>Minimum Quantity: Required, the quantity required to be purchased (X)</li>
 *           <li>Discounted Quantity: Required, the numer of free items (Y)</li>
 *           <li>Discounted Value: Required, should be 100 (for 100% off, i.e. free); any value other than 100 will
 *           result in a "Buy X Get Y at Z Off" type discount</li>
 *           <li>"Buy X Get Y At Z Off"-based: Discount where buying a certain number of items (X) qualifies for an
 *           additional set of items (Y) at percentage (Z) off</li>
 *           <li>Example: "Buy 1 Get 1 50% off"</li>
 *       </ul>
 *   </ul>
 * <li>"Buy X Get Y At Z Percentage Off"-based: Discount where buying a certain number of items (X) qualifies for an
 * additional set of items (Y) at percentage (Z) off</li>
 *   <ul>
 *      <li>Example: "Buy 1 Get 1 50% off"</li>
 *      <li>Attributes:</li>
 *      <ul>
 *          <li>Type: "buyXgetY"</li>
 *          <li>Minimum Quantity: Required, the quantity required to be purchased (X)</li>
 *          <li>Discounted Quantity: Required, the numer of free items (Y)</li>
 *          <li>Discounted Value: Required, the percentage off for the additional items (e.g. 50 for 50% off); must be
 *          between 0 and 100</li>
 *          <li>Applicable Category: Optional, to specify a category of items to which the discount applies; defaults
 *          to all categories</li>
 *          <li>Applicable UPCs: Optional, to specify a list of UPCs to which the discount applies; defaults to all UPCs
 *          </li>
 *      </uL>
 * </u1>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Discount {

    private String id;
    private String type;
    private BigDecimal value;
    private String description;
    private int minQuantity;
    private int discountedQuantity;
    private BigDecimal discountedValue;
    private String applicableCategory;
    private Set<String> applicableUpcs;
}