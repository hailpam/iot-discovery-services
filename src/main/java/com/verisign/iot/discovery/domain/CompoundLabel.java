
package com.verisign.iot.discovery.domain;

import com.verisign.iot.discovery.commons.Constants;
import java.util.Objects;

/**
 * Class to capture a compound label, i.e. built up by 'type', 'subtype' and 'protocol'. The only
 * needed part is the 'label'.
 *
 * The input format is: <code>'label[:sublabel:proto>|:proto]'</code>, the separator is ':'.
 *
 * @author pmaresca
 * @version 1.0
 * @since Oct 8, 2015
 */
public class CompoundLabel
{
    private static final String SEPARATOR = ":";

    /** A string label specifying a type or simply a name.*/
    private final String type;
    /** A string label specifying a subtype or simply a name.*/
    private final String subType;
    /** A string specifying a protocol (e.g. TCP or UDP). */
    private final String proto;


    /**
     * Takes a string label and matches it against the expected separator.
     *
     * @param label A <code>String</code> supposed to be formatted as above.
     *
     * @return  <code>true</code> iff the label respect the pattern
     */
    public final static boolean isCompound(String label)
    {
        if(label == null || label.isEmpty())
            throw new IllegalArgumentException("Input label cannot be NULL or EMPTY");

        if(label.length() > 1)
            return label.contains(SEPARATOR);
        else
            return false;
    }

    /**
     * Splits up a label into its components. The label is supposed to follow the above specified
     * pattern: '<code>'label[:sublabel:proto>|:proto]'</code>'.
     *
     * @param label A <code>String</code> containing the label formatted like: '<code>'label[:sublabel:proto>|:proto]'</code>'
     *
     * @return  An array with three elements: label (index 0), sublabel (index 1), proto (index 2 or index 1)
     */
    public final static String[] labelComponents(String label)
    {
        String[] parts = new String[3];
        parts[0] = "";
        parts[1] = "";
        parts[2] = "";
        
        if(isCompound(label) && label.length() > 1) {
            String[] splitted = label.split(SEPARATOR);
            if(splitted[0].isEmpty())
                throw new IllegalArgumentException("Malformed 'label': input label should respect the format: '<label[<:sublabel:proto>|<:proto>]>'");
            else
                parts[0] = splitted[0];
            if(splitted.length > 1) {
                if(!splitted[1].isEmpty() && (splitted[1].equalsIgnoreCase(Constants.TCP.replaceAll("_", ""))
                        || splitted[1].equalsIgnoreCase(Constants.UDP.replaceAll("_", "")))) {
                    parts[2] = splitted[1].toLowerCase();

                    return parts;
                } else
                    parts[1] = splitted[1];
            }
            if(splitted.length > 2)
                parts[2] = splitted[2].toLowerCase();

            return parts;
        } else
            throw new IllegalArgumentException("Input label should respect the format: '<label[<:sublabel:proto>|<:proto>]>'");
    }

    public CompoundLabel(String type) { this(type, ""); }

    public CompoundLabel(String type, String subType) { this(type, subType, Constants.TCP); }

    public CompoundLabel(String type, String subType, String proto) { this.type = type; this.subType = subType; this.proto = proto; }

    public String getType() { return type; }

    public String getSubType() { return subType; }

    public String getProto() { return proto; }


    /**
     * Check the subtype presence.
     *
     * @return  <code>true</code> iff any subtype is present
     */
    public boolean hasSubType() { return !(subType == null || subType.isEmpty()); }

    public String prefixString()
    {
        if(type.isEmpty())
            throw new IllegalStateException("Type is empty: unable to build a prefix string");
        if(proto.isEmpty())
            throw new IllegalStateException("Protocol is empty: unable to build a prefix string");

        StringBuilder prefix = new StringBuilder();
        prefix.append("_");
        if(subType != null && !subType.isEmpty()) {
            prefix.append(subType);
            prefix.append(Constants.DNS_LABEL_DELIMITER);
            prefix.append(Constants.SUBTYPE);
            prefix.append(Constants.DNS_LABEL_DELIMITER);
            prefix.append("_");
        }
        prefix.append(type);
        prefix.append(Constants.DNS_LABEL_DELIMITER);
        prefix.append("_");
        prefix.append((proto.contains("_")?proto.subSequence(1, proto.length()):proto));
        prefix.append(Constants.DNS_LABEL_DELIMITER);

        return prefix.toString();
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.type);
        hash = 89 * hash + Objects.hashCode(this.subType);

        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CompoundLabel other = (CompoundLabel) obj;
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.subType, other.subType)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() { return "ServiceType {" + "type=" + type + ", subType=" + subType + '}'; }

}
