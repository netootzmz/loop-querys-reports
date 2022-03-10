package com.smart.ecommerce.queries.service.impl;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.smart.ecommerce.entity.admin.Group;
import com.smart.ecommerce.entity.checkout.Membership;
import com.smart.ecommerce.entity.configuration.ClientAccountInfo;
import com.smart.ecommerce.entity.configuration.ClientBillingDetail;
import com.smart.ecommerce.entity.core.CoreErrorCode;
import com.smart.ecommerce.entity.checkout.ReferencePaymentDispersion;
import com.smart.ecommerce.queries.exception.SmartServicePlatformMyException;
import com.smart.ecommerce.queries.model.dto.*;
import com.smart.ecommerce.queries.repository.*;
import com.smart.ecommerce.queries.service.DepositsAndMovementsServPtalService;
import com.smart.ecommerce.queries.service.TransactionsServPtalService;
import com.smart.ecommerce.queries.util.ErrorCode;
import com.smart.ecommerce.queries.util.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.smart.ecommerce.queries.util.Constants.*;
import static com.smart.ecommerce.queries.util.ConvertDates.convertDateToStr;
import static com.smart.ecommerce.queries.util.GetValuesJsonUtils.*;
import static java.util.Objects.isNull;

/**
 * <code>TransactionsServPtalServiceImpl</code>.
 *
 * @author Adrian Pantoja
 * @version 1.0
 */
@Slf4j
@Service("DepositsAndMovementsServPtalService")
public class DepositsAndMovementsServPtalServiceImpl implements DepositsAndMovementsServPtalService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Resource private TransactionsServPtalRepository transactionsDetailRepository;

  @Resource private ErrorCodeRepository codeDao;

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private MembershipRepository membershipRepository;

  @Autowired
  private DepositsAndMovementsServPtalRepository depositsAndMovementsServPtalRepository;

  @Autowired
  private GroupRepository groupRepository;

  private static final String KEY_RESULTS = "results";
  private static final String LOG_ERROR_TRACE = "Error: ";
  private static final Integer GROUP_LEVEL_ID_REASON_SOCIAL = 4;

  @Override
  public GenericResponse getDepositsAndMovementsServPtal(String idOperation, DepositsAndMovementsServPtalDto dto, InfoTokenDto infoTokenDto) {
    GenericResponse response = new GenericResponse();
    List<CoreErrorCode> listCodes = codeDao.getAll(infoTokenDto.getLanguage_id());
    CoreErrorCodeDto errorItem = null;
    Map<String, Object> informationResponse = new HashMap<>();

    try {

      Map<String, Object> userInfo = clientRepository.getUserInfo( infoTokenDto.getUser_by_register() );

      if( userInfo.isEmpty() ) {
        logger.info( "UserInfo es vacío" );
        errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_PARAMETROS_INCOMPLETOS);
        return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
      } else {

          userInfo.entrySet().forEach(entry -> {
              System.out.println(entry.getKey() + " " + entry.getValue());
          });

          if( !dto.getStartDate().isEmpty()  ) {
              errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_PARAMETROS_INCOMPLETOS);
              return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
          }

          if( !dto.getEndDate().isEmpty()  ) {
              errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_PARAMETROS_INCOMPLETOS);
              return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
          }

          if( !dto.getRfc().isEmpty() && dto.getPaymentReference().isEmpty() && dto.getGroupId().isEmpty() &&
                  dto.getGrouperId().isEmpty() && dto.getReasonSocialId().isEmpty() && dto.getBranchId().isEmpty() ) {
              List<Map<String, Object>> listReasonSocials =
                      clientRepository.getClientBillingDetailByRFCAndClientId( dto.getRfc(), userInfo.get("client").toString() );

              if( listReasonSocials.size() > 0 ) {
                  StringJoiner sj = new StringJoiner( ", ");
                  for ( Map<String, Object> reasonSocial : listReasonSocials ) {
                      sj.add( reasonSocial.get("group_id").toString() );
                  }

                  logger.info( "listReasonSocials size", listReasonSocials.size() );
                  logger.info( "string joiner: ", sj.toString() );
              } else {
                  errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_PARAMETROS_INCOMPLETOS);
                  return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
              }
          }


/*

          // Inicio si se filtra con rfc
          if( !dto.getRfc().isEmpty() ) {

              // Inicio si se filtra por sucursal
              if( !dto.getBranchId().isEmpty() ) {
                  List<Map<String,Object>> listClientBillingDetail = clientRepository.getClientBillingDetailByRFCAndGroupId( dto.getRfc(), dto.getBranchId() );

                  if ( listClientBillingDetail.size() > 0 ) {
                      // Inicio si se filtra por clabe
                      if( !dto.getClabe().isEmpty() ) {
                          List<Map<String,Object>> listClientAccountInfo = clientRepository.getClientAccountInfoByClabeAndGroupId( dto.getClabe(), dto.getBranchId() );

                          if( listClientAccountInfo.size() > 0 ) {
                              List<Map<String, Object>> membership = membershipRepository.getMembershipByGroupId( dto.getBranchId() );

                              if( !isNull( membership ) ) {

                                  List<ReferencePaymentDispersionResponseDto> listReferencePaymentDispersion;

                                  if( !dto.getPaymentReference().isEmpty() ) {

                                      listReferencePaymentDispersion =
                                              depositsAndMovementsServPtalRepository.getDepositsAndMovementsServPtal(
                                                      dto.getStartDate(),
                                                      dto.getEndDate(),
                                                      membership.get(0).get("membership").toString(),
                                                      dto.getPaymentReference(),
                                                      dto.getClabe(),
                                                      userInfo.get("client").toString()
                                              );
                                  } else {
                                      listReferencePaymentDispersion =
                                              depositsAndMovementsServPtalRepository.getDepositsAndMovementsServPtal(
                                                      dto.getStartDate(),
                                                      dto.getEndDate(),
                                                      membership.get(0).get("membership").toString(),
                                                      "",
                                                      dto.getClabe(),
                                                      userInfo.get("client").toString()
                                              );
                                  }

                                  if( listReferencePaymentDispersion.size() > 0 ) {
                                      informationResponse.put( KEY_RESULTS, listReferencePaymentDispersion);
                                      errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_PROCESADO_CORRECTAMENTE);
                                      return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), informationResponse );
                                  } else {
                                      errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                      return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                                  }

                              } else {
                                  errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                  return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                              }
                          }
                      } else {
                          ClientAccountInfo clientAccountInfo = clientRepository.getClientAccountInfoByGroupId( dto.getBranchId() );

                          if ( !isNull(clientAccountInfo) ) {
                              List<Map<String, Object>> membership = membershipRepository.getMembershipByGroupId( dto.getBranchId() );

                              if( !isNull( membership ) ) {

                                  List<ReferencePaymentDispersionResponseDto> listReferencePaymentDispersion;

                                  if( !dto.getPaymentReference().isEmpty() ) {
                                      listReferencePaymentDispersion =
                                              depositsAndMovementsServPtalRepository.getDepositsAndMovementsServPtal(
                                                      dto.getStartDate(),
                                                      dto.getEndDate(),
                                                      membership.get(0).get("membership").toString(),
                                                      dto.getPaymentReference(),
                                                      dto.getClabe(),
                                                      userInfo.get("client").toString()
                                              );
                                  } else {
                                      listReferencePaymentDispersion =
                                              depositsAndMovementsServPtalRepository.getDepositsAndMovementsServPtal(
                                                      dto.getStartDate(),
                                                      dto.getEndDate(),
                                                      membership.get(0).get("membership").toString(),
                                                      "",
                                                      dto.getClabe(),
                                                      userInfo.get("client").toString()
                                              );
                                  }

                                  if( listReferencePaymentDispersion.size() > 0 ) {
                                      informationResponse.put( KEY_RESULTS, listReferencePaymentDispersion);
                                      errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_PROCESADO_CORRECTAMENTE);
                                      return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), informationResponse );
                                  } else {
                                      errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                      return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                                  }

                              } else {
                                  errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                  return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                              }
                          } else {
                              errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                              return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                          }
                      }
                  } else {
                      errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                      return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                  }
              } else {
                  // Inicio si se filtra por razón social
                  if ( !dto.getReasonSocialId().isEmpty() ) {

                      List<Map<String, String>> listBranchs = groupRepository.getGroupsByLevelIdAndParentGroupId(
                              GROUP_LEVEL_ID_REASON_SOCIAL, dto.getReasonSocialId()
                      );

                      if( listBranchs.size() > 0 ) {

                          for ( Map<String, String> branch : listBranchs ) {

                              List<Map<String,Object>> listClientBillingDetail = clientRepository.getClientBillingDetailByRFCAndGroupId( dto.getRfc(), branch.get("groupId") );

                              if( listClientBillingDetail.size() > 0 ) {
                                  // Inicio si se filtra por clabe
                                  if( !dto.getClabe().isEmpty() ) {
                                      List<Map<String,Object>> listClientAccountInfo = clientRepository.getClientAccountInfoByClabeAndGroupId( dto.getClabe(), branch.get("groupId") );

                                      if( listClientAccountInfo.size() > 0 ) {
                                          List<Map<String,Object>> membership = membershipRepository.getMembershipByGroupId( branch.get("groupId") );

                                          if( !isNull( membership ) ) {
                                              List<ReferencePaymentDispersionResponseDto> listReferencePaymentDispersion =
                                                      depositsAndMovementsServPtalRepository.getDepositsAndMovementsServPtal(
                                                              dto.getStartDate(),
                                                              dto.getEndDate(),
                                                              membership.get(0).get("membership").toString(),
                                                              dto.getPaymentReference(),
                                                              dto.getClabe(),
                                                              userInfo.get("client").toString()
                                                      );

                                              if( listReferencePaymentDispersion.size() > 0 ) {
                                                  informationResponse.put( KEY_RESULTS, listReferencePaymentDispersion);
                                                  errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_PROCESADO_CORRECTAMENTE);
                                                  return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), informationResponse );
                                              } else {
                                                  errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                                  return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                                              }

                                          } else {
                                              errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                              return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                                          }
                                      }
                                  } else {
                                      ClientAccountInfo clientAccountInfo = clientRepository.getClientAccountInfoByGroupId( dto.getBranchId() );

                                      if ( !isNull(clientAccountInfo) ) {
                                          List<Map<String,Object>> membership = membershipRepository.getMembershipByGroupId( dto.getBranchId() );

                                          if( !isNull( membership ) ) {
                                              List<ReferencePaymentDispersionResponseDto> listReferencePaymentDispersion =
                                                      depositsAndMovementsServPtalRepository.getDepositsAndMovementsServPtal(
                                                              dto.getStartDate(),
                                                              dto.getEndDate(),
                                                              membership.get(0).get("membership").toString(),
                                                              dto.getPaymentReference(),
                                                              clientAccountInfo.getClabe(),
                                                              userInfo.get("client").toString()
                                                      );

                                              if( listReferencePaymentDispersion.size() > 0 ) {
                                                  informationResponse.put( KEY_RESULTS, listReferencePaymentDispersion);
                                                  errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_PROCESADO_CORRECTAMENTE);
                                                  return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), informationResponse );
                                              } else {
                                                  errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                                  return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                                              }

                                          } else {
                                              errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                              return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                                          }
                                      } else {
                                          errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                          return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                                      }
                                  }
                              } else {
                                  errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                  return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                              }
                          }

                      } else {
                          errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                          return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                      }
                  } else {
                      // Inicio si se filtra por agrupador
                      if( !dto.getGrouperId().isEmpty() ) {
                          errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                          return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                      } else {
                          // Inicio si se filtra por grupo
                          if( !dto.getGroupId().isEmpty() ) {
                              errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                              return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                          } else {
                              errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                              return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                          }
                      }
                  }
              }


          } else {  // Si no se filtra por rfc
              // Inicio si se filtra por clabe
              if( !dto.getClabe().isEmpty() ) {

                  if( !dto.getBranchId().isEmpty() ) {

                      List<Map<String,Object>> listClientAccountInfo = clientRepository.getClientAccountInfoByClabeAndGroupId( dto.getClabe(), dto.getBranchId() );

                      if( listClientAccountInfo.size() > 0 ) {
                          List<Map<String,Object>> membership = membershipRepository.getMembershipByGroupId( dto.getBranchId() );

                          if( !isNull( membership ) ) {

                              List<ReferencePaymentDispersionResponseDto> listReferencePaymentDispersion;

                              if( !dto.getPaymentReference().isEmpty() ) {
                                  listReferencePaymentDispersion =
                                          depositsAndMovementsServPtalRepository.getDepositsAndMovementsServPtal(
                                                  dto.getStartDate(),
                                                  dto.getEndDate(),
                                                  membership.get(0).get("membership").toString(),
                                                  dto.getPaymentReference(),
                                                  dto.getClabe(),
                                                  userInfo.get("client").toString()
                                          );
                              } else {
                                  listReferencePaymentDispersion =
                                          depositsAndMovementsServPtalRepository.getDepositsAndMovementsServPtal(
                                                  dto.getStartDate(),
                                                  dto.getEndDate(),
                                                  membership.get(0).get("membership").toString(),
                                                  "",
                                                  dto.getClabe(),
                                                  userInfo.get("client").toString()
                                          );
                              }

                              if( listReferencePaymentDispersion.size() > 0 ) {
                                  informationResponse.put( KEY_RESULTS, listReferencePaymentDispersion);
                                  errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_PROCESADO_CORRECTAMENTE);
                                  return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), informationResponse );
                              } else {
                                  errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                  return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                              }

                          } else {
                              errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                              return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                          }
                      }

                  } else {
                      // Inicio si se filtra por razón social
                      if ( !dto.getReasonSocialId().isEmpty() ) {

                          List<Map<String, String>> listBranchs = groupRepository.getGroupsByLevelIdAndParentGroupId(
                                  GROUP_LEVEL_ID_REASON_SOCIAL, dto.getReasonSocialId()
                          );

                          if( listBranchs.size() > 0 ) {

                              for ( Map<String, String> branch : listBranchs ) {

                                  List<Map<String,Object>> listClientBillingDetail = clientRepository.getClientBillingDetailByRFCAndGroupId( dto.getRfc(), branch.get("groupId") );

                                  if( listClientBillingDetail.size() > 0 ) {
                                      // Inicio si se filtra por clabe
                                      if( !dto.getClabe().isEmpty() ) {
                                          List<Map<String,Object>> listClientAccountInfo = clientRepository.getClientAccountInfoByClabeAndGroupId( dto.getClabe(), branch.get("groupId") );

                                          if( listClientAccountInfo.size() > 0 ) {
                                              List<Map<String,Object>> membership = membershipRepository.getMembershipByGroupId( branch.get("groupId") );

                                              if( !isNull( membership ) ) {
                                                  List<ReferencePaymentDispersionResponseDto> listReferencePaymentDispersion =
                                                          depositsAndMovementsServPtalRepository.getDepositsAndMovementsServPtal(
                                                                  dto.getStartDate(),
                                                                  dto.getEndDate(),
                                                                  membership.get(0).get("membership").toString(),
                                                                  dto.getPaymentReference(),
                                                                  dto.getClabe(),
                                                                  userInfo.get("client").toString()
                                                          );

                                                  if( listReferencePaymentDispersion.size() > 0 ) {
                                                      informationResponse.put( KEY_RESULTS, listReferencePaymentDispersion);
                                                      errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_PROCESADO_CORRECTAMENTE);
                                                      return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), informationResponse );
                                                  } else {
                                                      errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                                      return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                                                  }

                                              } else {
                                                  errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                                  return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                                              }
                                          }
                                      } else {
                                          ClientAccountInfo clientAccountInfo = clientRepository.getClientAccountInfoByGroupId( dto.getBranchId() );

                                          if ( !isNull(clientAccountInfo) ) {
                                              List<Map<String,Object>> membership = membershipRepository.getMembershipByGroupId( dto.getBranchId() );

                                              if( !isNull( membership ) ) {
                                                  List<ReferencePaymentDispersionResponseDto> listReferencePaymentDispersion =
                                                          depositsAndMovementsServPtalRepository.getDepositsAndMovementsServPtal(
                                                                  dto.getStartDate(),
                                                                  dto.getEndDate(),
                                                                  membership.get(0).get("membership").toString(),
                                                                  dto.getPaymentReference(),
                                                                  clientAccountInfo.getClabe(),
                                                                  userInfo.get("client").toString()
                                                          );

                                                  if( listReferencePaymentDispersion.size() > 0 ) {
                                                      informationResponse.put( KEY_RESULTS, listReferencePaymentDispersion);
                                                      errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_PROCESADO_CORRECTAMENTE);
                                                      return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), informationResponse );
                                                  } else {
                                                      errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                                      return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                                                  }

                                              } else {
                                                  errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                                  return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                                              }
                                          } else {
                                              errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                              return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                                          }
                                      }
                                  } else {
                                      errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                      return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                                  }
                              }

                          } else {
                              errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                              return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                          }
                      } else {
                          // Inicio si se filtra por agrupador
                          if( !dto.getGrouperId().isEmpty() ) {
                              errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                              return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                          } else {
                              // Inicio si se filtra por grupo
                              if( !dto.getGroupId().isEmpty() ) {
                                  errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                  return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                              } else {
                                  errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                                  return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                              }
                          }
                      }
                  }

              } else {
                  List<ReferencePaymentDispersionResponseDto> results =
                          depositsAndMovementsServPtalRepository.getDepositsAndMovementsServPtal(
                                  dto.getStartDate(),
                                  dto.getEndDate(),
                                  dto.getBranchId(),
                                  dto.getPaymentReference(),
                                  dto.getClabe(),
                                  userInfo.get("client").toString()
                          );

                  if ( !results.isEmpty() ) {
                      response.setCodeStatus("00");
                      response.setMessage(ErrorCode.getError(listCodes, "00").getMessage());
                      Map<String, Object> information = new HashMap<>();
                      information.put(KEY_RESULTS, results);
                      response.setInformation(information);
                  } else {
                      errorItem = ErrorCode.getError(listCodes, ErrorCode.ERROR_CODE_SIN_REGISTROS);
                      return new GenericResponse(errorItem.getCode(), errorItem.getMessage(), new HashMap<>() );
                  }
                  return response;
              }
          }
          // Fin si se filtra por rfc
*/

      }

      return response;

    } catch (Exception e) {
      StringBuilder errorStack = new StringBuilder();
      for (StackTraceElement er : e.getStackTrace()) {
        errorStack.append(LOG_ERROR_TRACE);
        errorStack.append(er.getClassName() + SPACE);
        errorStack.append(er.getFileName() + SPACE);
        errorStack.append(er.getLineNumber() + SPACE);
        errorStack.append(er.getMethodName() + SPACE);
        errorStack.append("");
      }
      String errorTrace = errorStack.toString();
      log.info("Error Transaction ServPtal: {}", errorTrace);
      response.setCodeStatus("03");
      //response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), e.getMessage()))
      response.setMessage(String.format(ErrorCode.getError(listCodes, "03").getMessage(), errorTrace));
      e.printStackTrace();
      return response;

    }
  }
}
