// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: SyncSoulCount.proto

package com.guaji.game.protocol;

public final class SyncSoulCount {
  private SyncSoulCount() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface SyncSoulCountRetOrBuilder
      extends com.google.protobuf.MessageOrBuilder {

    // required int32 itemId = 1;
    /**
     * <code>required int32 itemId = 1;</code>
     */
    boolean hasItemId();
    /**
     * <code>required int32 itemId = 1;</code>
     */
    int getItemId();

    // required int32 soulCount = 2;
    /**
     * <code>required int32 soulCount = 2;</code>
     */
    boolean hasSoulCount();
    /**
     * <code>required int32 soulCount = 2;</code>
     */
    int getSoulCount();

    // required bool isActivated = 3;
    /**
     * <code>required bool isActivated = 3;</code>
     */
    boolean hasIsActivated();
    /**
     * <code>required bool isActivated = 3;</code>
     */
    boolean getIsActivated();
  }
  /**
   * Protobuf type {@code SyncSoulCountRet}
   */
  public static final class SyncSoulCountRet extends
      com.google.protobuf.GeneratedMessage
      implements SyncSoulCountRetOrBuilder {
    // Use SyncSoulCountRet.newBuilder() to construct.
    private SyncSoulCountRet(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private SyncSoulCountRet(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final SyncSoulCountRet defaultInstance;
    public static SyncSoulCountRet getDefaultInstance() {
      return defaultInstance;
    }

    public SyncSoulCountRet getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private SyncSoulCountRet(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              itemId_ = input.readInt32();
              break;
            }
            case 16: {
              bitField0_ |= 0x00000002;
              soulCount_ = input.readInt32();
              break;
            }
            case 24: {
              bitField0_ |= 0x00000004;
              isActivated_ = input.readBool();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.guaji.game.protocol.SyncSoulCount.internal_static_SyncSoulCountRet_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.guaji.game.protocol.SyncSoulCount.internal_static_SyncSoulCountRet_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet.class, com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet.Builder.class);
    }

    public static com.google.protobuf.Parser<SyncSoulCountRet> PARSER =
        new com.google.protobuf.AbstractParser<SyncSoulCountRet>() {
      public SyncSoulCountRet parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new SyncSoulCountRet(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<SyncSoulCountRet> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    // required int32 itemId = 1;
    public static final int ITEMID_FIELD_NUMBER = 1;
    private int itemId_;
    /**
     * <code>required int32 itemId = 1;</code>
     */
    public boolean hasItemId() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required int32 itemId = 1;</code>
     */
    public int getItemId() {
      return itemId_;
    }

    // required int32 soulCount = 2;
    public static final int SOULCOUNT_FIELD_NUMBER = 2;
    private int soulCount_;
    /**
     * <code>required int32 soulCount = 2;</code>
     */
    public boolean hasSoulCount() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required int32 soulCount = 2;</code>
     */
    public int getSoulCount() {
      return soulCount_;
    }

    // required bool isActivated = 3;
    public static final int ISACTIVATED_FIELD_NUMBER = 3;
    private boolean isActivated_;
    /**
     * <code>required bool isActivated = 3;</code>
     */
    public boolean hasIsActivated() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>required bool isActivated = 3;</code>
     */
    public boolean getIsActivated() {
      return isActivated_;
    }

    private void initFields() {
      itemId_ = 0;
      soulCount_ = 0;
      isActivated_ = false;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;

      if (!hasItemId()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasSoulCount()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasIsActivated()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeInt32(1, itemId_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeInt32(2, soulCount_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeBool(3, isActivated_);
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, itemId_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, soulCount_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(3, isActivated_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code SyncSoulCountRet}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRetOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.guaji.game.protocol.SyncSoulCount.internal_static_SyncSoulCountRet_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.guaji.game.protocol.SyncSoulCount.internal_static_SyncSoulCountRet_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet.class, com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet.Builder.class);
      }

      // Construct using com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        itemId_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        soulCount_ = 0;
        bitField0_ = (bitField0_ & ~0x00000002);
        isActivated_ = false;
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.guaji.game.protocol.SyncSoulCount.internal_static_SyncSoulCountRet_descriptor;
      }

      public com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet getDefaultInstanceForType() {
        return com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet.getDefaultInstance();
      }

      public com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet build() {
        com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet buildPartial() {
        com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet result = new com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.itemId_ = itemId_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.soulCount_ = soulCount_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.isActivated_ = isActivated_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet) {
          return mergeFrom((com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet other) {
        if (other == com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet.getDefaultInstance()) return this;
        if (other.hasItemId()) {
          setItemId(other.getItemId());
        }
        if (other.hasSoulCount()) {
          setSoulCount(other.getSoulCount());
        }
        if (other.hasIsActivated()) {
          setIsActivated(other.getIsActivated());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasItemId()) {
          
          return false;
        }
        if (!hasSoulCount()) {
          
          return false;
        }
        if (!hasIsActivated()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.guaji.game.protocol.SyncSoulCount.SyncSoulCountRet) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      // required int32 itemId = 1;
      private int itemId_ ;
      /**
       * <code>required int32 itemId = 1;</code>
       */
      public boolean hasItemId() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required int32 itemId = 1;</code>
       */
      public int getItemId() {
        return itemId_;
      }
      /**
       * <code>required int32 itemId = 1;</code>
       */
      public Builder setItemId(int value) {
        bitField0_ |= 0x00000001;
        itemId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 itemId = 1;</code>
       */
      public Builder clearItemId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        itemId_ = 0;
        onChanged();
        return this;
      }

      // required int32 soulCount = 2;
      private int soulCount_ ;
      /**
       * <code>required int32 soulCount = 2;</code>
       */
      public boolean hasSoulCount() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required int32 soulCount = 2;</code>
       */
      public int getSoulCount() {
        return soulCount_;
      }
      /**
       * <code>required int32 soulCount = 2;</code>
       */
      public Builder setSoulCount(int value) {
        bitField0_ |= 0x00000002;
        soulCount_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 soulCount = 2;</code>
       */
      public Builder clearSoulCount() {
        bitField0_ = (bitField0_ & ~0x00000002);
        soulCount_ = 0;
        onChanged();
        return this;
      }

      // required bool isActivated = 3;
      private boolean isActivated_ ;
      /**
       * <code>required bool isActivated = 3;</code>
       */
      public boolean hasIsActivated() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>required bool isActivated = 3;</code>
       */
      public boolean getIsActivated() {
        return isActivated_;
      }
      /**
       * <code>required bool isActivated = 3;</code>
       */
      public Builder setIsActivated(boolean value) {
        bitField0_ |= 0x00000004;
        isActivated_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required bool isActivated = 3;</code>
       */
      public Builder clearIsActivated() {
        bitField0_ = (bitField0_ & ~0x00000004);
        isActivated_ = false;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:SyncSoulCountRet)
    }

    static {
      defaultInstance = new SyncSoulCountRet(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:SyncSoulCountRet)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_SyncSoulCountRet_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SyncSoulCountRet_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\023SyncSoulCount.proto\"J\n\020SyncSoulCountRe" +
      "t\022\016\n\006itemId\030\001 \002(\005\022\021\n\tsoulCount\030\002 \002(\005\022\023\n\013" +
      "isActivated\030\003 \002(\010B\031\n\027com.guaji.game.prot" +
      "ocol"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_SyncSoulCountRet_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_SyncSoulCountRet_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_SyncSoulCountRet_descriptor,
              new java.lang.String[] { "ItemId", "SoulCount", "IsActivated", });
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}
