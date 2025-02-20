package de.ptb.common.dcc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "CACHE")
public class CacheItem {

  @Id
  @Column(name = "ID", nullable = false, unique = true)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cached_item_seq_gen")
  @SequenceGenerator(name = "cached_item_seq_gen", sequenceName = "cached_item_seq")
  private Long id;

  @Column(name = "CALLBACK_URL")
  private String callbackUrl;

  @Column(name = "FILE_NAME", nullable = false)
  private String fileName;

  @Column(name = "MIME_TYPE", nullable = false)
  private String mimeType;

  @Lob
  @Column(name = "DATA", nullable = false)
  private byte[] fileContent;

  @Column(name = "CREATED_AT", nullable = false)
  private Date createdAt;
}
