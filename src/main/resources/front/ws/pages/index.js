import { useEffect, useRef, useState } from "react";
import useSWR from 'swr'
import PageContainer, { getUserId, getUserRoles, getUsername } from "@/components/pageContainer"
import styles from "@/styles/Home.module.css"
import Image from "next/image";

import { backUrl } from "@/components/pageContainer";
import Spinner from "@/components/spinner";
import Error from "@/components/error";

const fetcher = (...args) => fetch(...args).then(res => res.json());

export default function Home() {
  return (<PageContainer>
    <HomePage />
  </PageContainer>);
}

function HomePage() {
  const [selectedSection, setSelectedSection] = useState(0);
  const [selectedItem, setSelectedItem] = useState(null);
  const [appliedFilters, setAppliedFilters] = useState({ search: null, type: null, onlyMy: false });

  const [userId, setUserId] = useState();
  const [username, setUsername] = useState();

  useEffect(() => {
    setUserId(getUserId());
    setUsername(getUsername());
  }, [])

  function showSection(i) {
    setSelectedSection(i);
    setSelectedItem(null);
  }

  return (
    <section className={styles.main_grid}>
      <AllElementsTable userId={userId} username={username} selectedSection={selectedSection} showSection={showSection} setSelectedItem={setSelectedItem} appliedFilters={appliedFilters} setAppliedFilters={setAppliedFilters} />
      <OneElementPreview selectedItem={selectedItem} />
    </section >);
}

function AllElementsTable({ userId, username, selectedSection, showSection, setSelectedItem, appliedFilters, setAppliedFilters }) {
  return (
    <div className={styles.card} id="all_elements_table">
      <ToggleOptions selectedSection={selectedSection} showSection={showSection} />
      <FilterOptions appliedFilters={appliedFilters} setAppliedFilters={setAppliedFilters} />
      <MainArea userId={userId} username={username} selectedSection={selectedSection} appliedFilters={appliedFilters} setSelectedItem={setSelectedItem} />
    </div>
  );
}

function ToggleOptions({ selectedSection, showSection }) {
  return (
    <div className={styles.toggle_options}>
      <p onClick={() => showSection(0)} className={selectedSection == 0 ? styles.selected : ''}>All certificates</p>
      <div className={styles.separator}></div>
      <p onClick={() => showSection(1)} className={selectedSection == 1 ? styles.selected : ''}>My requests</p>
      <div className={styles.separator}></div>
      <p onClick={() => showSection(2)} className={selectedSection == 2 ? styles.selected : ''}>For signing</p>
    </div>
  );
}

function FilterOptions({ appliedFilters, setAppliedFilters }) {

  function handleSearchChange(event) {
    const newAppliedFilters = { search: event.target.value, type: appliedFilters['type'], onlyMy: appliedFilters['onlyMy'] };
    setAppliedFilters(newAppliedFilters);
  }

  function handleTypeChange(event) {
    const newAppliedFilters = { search: appliedFilters['search'], type: event.target.value, onlyMy: appliedFilters['onlyMy'] };
    setAppliedFilters(newAppliedFilters);
  }

  function handleCheckboxChange(event) {
    const newAppliedFilters = { search: appliedFilters['search'], type: appliedFilters['type'], onlyMy: event.target.checked };
    setAppliedFilters(newAppliedFilters);
  }

  return (
    <div className={styles.filter_options}>
      <input id="search" name="search" type="text" placeholder="Search..." onChange={handleSearchChange} />
      <select id="type" onChange={handleTypeChange}>
        <option value="ALL">ALL</option>
        <option value="ROOT">ROOT</option>
        <option value="INTERMEDIATE">INTERMEDIATE</option>
        <option value="END">END</option>
      </select>
      <div>
        <label htmlFor="onlyMy">Only my:</label>
        <input id="onlyMy" name="onlyMy" type="checkbox" onChange={handleCheckboxChange} />
      </div>
    </div>
  );
}

function MainArea({ userId, username, selectedSection, appliedFilters, setSelectedItem }) {
  let tableToRender = null;
  if (selectedSection == 0) {
    tableToRender = <AllCertificates appliedFilters={appliedFilters} setSelectedItem={setSelectedItem} />
  } else if (selectedSection == 1) {
    tableToRender = <MyRequests appliedFilters={appliedFilters} setSelectedItem={setSelectedItem} />
  } else if (selectedSection == 2) {
    tableToRender = <ForSigning userId={userId} username={username} appliedFilters={appliedFilters} setSelectedItem={setSelectedItem} />
  }
  return (
    <div id="main_area">
      {tableToRender}
    </div>
  );
}

function AllCertificates({ appliedFilters, setSelectedItem }) {
  function handleRowClick(certificate) {
    setSelectedItem(certificate);
  }

  const { certificates, isLoading, isError } = getAllCertificates();

  if (isLoading) return <Spinner />
  if (isError) return <Error />

  const certificatesData = filterResults(certificates.content, appliedFilters);

  if (certificatesData == null || certificatesData.length == 0) return null;

  const columnNames = Object.keys(certificatesData[0]);

  return (
    <table className={styles.main_table}>
      <thead>
        <tr>
          <th>valid</th>
          {columnNames.filter(column => ['serialNumber', 'owner', 'issuer', 'type', 'notBefore', 'notAfter'].includes(column)).map(column => <th key={column}>{column}</th>)}
        </tr>
      </thead>
      <tbody>
        {certificatesData.map(item => <tr onClick={() => handleRowClick(item)} key={item['serialNumber']}><td>{item['valid'] ? <div className={styles.validCircle}></div> : <div className={styles.invalidCircle}></div>}</td>{Object.keys(item).filter(column => ['serialNumber', 'owner', 'issuer', 'type', 'notBefore', 'notAfter'].includes(column)).map(itemKey => <td key={`${item['serialNumber']}-${itemKey}`}>{itemKey != 'serialNumber' ? item[itemKey] : `...${item[itemKey].slice(-5)}`}</td>)}</tr>)}
      </tbody>
    </table>
  );

}

function MyRequests({ appliedFilters, setSelectedItem }) {
  function handleRowClick(certificate) {
    setSelectedItem(certificate);
  }

  const { requests, isLoading, isError } = getAllUserRequests(userId);

  if (isLoading) return <Spinner />
  if (isError) return <Error />

  const requestsData = filterResults(requests, appliedFilters);

  if (requestsData == null || requestsData.length == 0) return null;

  const columnNames = Object.keys(requestsData[0]);

  return (
    <table className={styles.main_table}>
      <thead>
        <tr>
          <th>status</th>
          {columnNames.filter(column => ['requestId', 'subjectId', 'issuerCertificateId', 'requestedDate', 'certificateType'].includes(column)).map(column => <th key={column}>{column}</th>)}
        </tr>
      </thead>
      <tbody>
        {console.log(requestsData)}
        {requestsData.map(item => <tr onClick={() => handleRowClick(item)} key={item['requestId']}><td>{item['status'] == 'APPROVED' ? <div className={styles.validCircle}></div> : item['status'] == 'DENIED' ? <div className={styles.invalidCircle}></div> : <div className={styles.pendingCircle}></div>}</td>{Object.keys(item).filter(column => ['requestId', 'subjectId', 'issuerCertificateId', 'requestedDate', 'certificateType'].includes(column)).map(itemKey => <td key={`${item['subjectId']}-${itemKey}`}>{itemKey != 'issuerCertificateId' ? item[itemKey] : `...${item[itemKey] ? item[itemKey].slice(-5) : 'self signed'}`}</td>)}</tr>)}
      </tbody>
    </table>
  );
}

function ForSigning({ userId, username, appliedFilters, setSelectedItem }) {
  function handleRowClick(certificate) {
    setSelectedItem(certificate);
  }

  const { requests, isLoading, isError } = getAllRequestsToReview(userId);

  if (isLoading) return <Spinner />
  if (isError) return <Error />

  const requestsData = filterResults(requests, appliedFilters);

  if (requestsData == null || requestsData.length == 0) return null;

  const columnNames = Object.keys(requestsData[0]);

  return (
    <table className={styles.main_table}>
      <thead>
        <tr>
          <th>status</th>
          {columnNames.filter(column => ['requestId', 'subjectId', 'issuerCertificateId', 'requestedDate', 'certificateType'].includes(column)).map(column => <th key={column}>{column}</th>)}
        </tr>
      </thead>
      <tbody>
        {requestsData.map(item => <tr onClick={() => handleRowClick(item)} key={item['requestId']}><td>{item['status'] == 'APPROVED' ? <div className={styles.validCircle}></div> : item['status'] == 'DENIED' ? <div className={styles.invalidCircle}></div> : <div className={styles.pendingCircle}></div>}</td>{Object.keys(item).filter(column => ['requestId', 'subjectId', 'issuerCertificateId', 'requestedDate', 'certificateType'].includes(column)).map(itemKey => <td key={`${item['subjectId']}-${itemKey}`}>{item[itemKey]}</td>)}</tr>)}
      </tbody>
    </table>
  );
}

function getAllCertificates() {
  const { data, error, isLoading } = useSWR(`${backUrl}/api/certificate/all`, fetcher)

  return {
    certificates: data,
    isLoading,
    isError: error
  }
}

function getAllUserRequests(userId) {
  const { data, error, isLoading } = useSWR(`${backUrl}/api/certificate/all-certificate-requests/${userId}`, fetcher)

  return {
    requests: data,
    isLoading,
    isError: error
  }
}

function getAllRequestsToReview(userId) {
  const { data, error, isLoading } = useSWR(`${backUrl}/api/certificate/all-requests-to-review/${userId}`, fetcher)

  return {
    requests: data,
    isLoading,
    isError: error
  }
}

function filterResults(data, filters) {
  let dataThatFulfillsFilters = JSON.parse(JSON.stringify(data))
  if (filters['search'] != null && filters['search'] != '') {
    for (let item of dataThatFulfillsFilters) {
      let fulfillsFilters = false;
      for (let key in item) {
        if (String(item[key]).toLowerCase().includes(filters['search'].toLowerCase())) {
          fulfillsFilters = true
          break;
        }
      }
      if (!fulfillsFilters) {
        dataThatFulfillsFilters.splice(dataThatFulfillsFilters.indexOf(item), 1);
      }
    }
  }
  if (filters['type'] != null && filters['type'] != 'ALL') {
    dataThatFulfillsFilters = dataThatFulfillsFilters.filter(item => item['type'] == filters['type'] || item['certificateType'] == filters['type']);
  }
  if (filters['onlyMy']) {
    dataThatFulfillsFilters = dataThatFulfillsFilters.filter(item => item['owner'] == username || item['subjectId'] == username);
  }
  return dataThatFulfillsFilters;
}

function OneElementPreview({ selectedItem }) {
  if (selectedItem == undefined) {
    return null
  }
  else if (Object.keys(selectedItem).includes('serialNumber')) {
    return <CertificatePreview selectedItem={selectedItem} />;
  }
  else if (Object.keys(selectedItem).includes('status') && selectedItem.subjectId == userId) {
    return <UserCertificateRequestPreview selectedItem={selectedItem} />
  } else {
    return <CertificateRequestNeedsApproval selectedItem={selectedItem} />
  }
}

function CertificatePreview({ selectedItem }) {
  return (<div className={`${styles.card} ${styles.preview}`} id="one_element_preview">
    <h2>Certificate</h2>
    <ul className={styles.certInfo}>
      <li><span className={styles.certInfoLabel}>Serial number: </span><span>{selectedItem.serialNumber}</span></li>
      <li><span className={styles.certInfoLabel}>Owner: </span><span>{selectedItem.owner}</span></li>
      <li><span className={styles.certInfoLabel}>Issuer: </span><span>{selectedItem.issuer}</span></li>
      <li><span className={styles.certInfoLabel}>Valid till: </span><span>{selectedItem.notAfter}</span></li>
      <li><span className={styles.certInfoLabel}>Type: </span><span>{selectedItem.type}</span></li>
    </ul>
    <hr />
    {selectedItem.type != 'END' && <RequestCertificateAction issuerSerialNumber={selectedItem.serialNumber} />}
    {selectedItem.owner == username && <InvalidateButton serialNumber={selectedItem.serialNumber} />}
    <DownloadButton serialNumber={selectedItem.serialNumber} />
  </div>)
}

function UserCertificateRequestPreview({ selectedItem }) {
  return (<div className={`${styles.card} ${styles.preview}`} id="one_element_preview">
    <h2>Certificate Request</h2>
    <ul className={styles.certInfo}>
      <li><span className={styles.certInfoLabel}>Issuer certificate id: </span><span>{selectedItem.issuerCertificateId}</span></li>
      <li><span className={styles.certInfoLabel}>Requested date: </span><span>{selectedItem.requestedDate}</span></li>
      <li><span className={styles.certInfoLabel}>Type: </span><span>{selectedItem.certificateType}</span></li>
      <li><span className={styles.certInfoLabel}>Status: </span><span>{selectedItem.status}</span></li>
      {selectedItem.status == 'DENIED' && <li><span className={styles.certInfoLabel}>Denial reason: </span><span>{selectedItem.denyReason}</span></li>}
    </ul>
  </div>)
}

function CertificateRequestNeedsApproval({ selectedItem }) {
  return (<div className={`${styles.card} ${styles.preview}`} id="one_element_preview">
    <h2>Approve ceritificate request</h2>
    <ul className={styles.certInfo}>
      <li><span className={styles.certInfoLabel}>Issuer certificate id: </span><span>{selectedItem.issuerCertificateId}</span></li>
      <li><span className={styles.certInfoLabel}>Requested date: </span><span>{selectedItem.requestedDate}</span></li>
      <li><span className={styles.certInfoLabel}>Type: </span><span>{selectedItem.certificateType}</span></li>
    </ul>
    <ApproveBtn requestId={selectedItem['requestId']} />
    <DenyBtn requestId={selectedItem['requestId']} />
  </div>)
}

function ApproveBtn({ requestId }) {
  return <div className={styles.approveBtn} onClick={() => approveCertificateRequest(requestId)}>
    <div>
      <a>Approve</a>
    </div>
    <div className={styles.imgDiv}>
      <Image src="/images/approveCertificateIcon.png" width={24} height={24} alt="approveCertificateIcon"></Image>
    </div>
  </div>
}

async function approveCertificateRequest(requestId) {
  const requestOptions = {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
  };

  const response = await fetch(`${backUrl}/api/certificate/approve/${requestId}`, requestOptions);
  const data = await response.json();
  if (data.status == 200) {
    alert("Succesfully approved certificate.")
  } else {
    alert("Something went wrong!")
  }
}

function DenyBtn({ requestId }) {
  const [denyReason, setDenyReason] = useState(null)
  const ref = useRef(null)

  function handleDenyReason(event) {
    setDenyReason(event.target.value)
  }

  function handleDenial() {
    if (denyReason == null) {
      alert("You must enter a reason")
    } else {
      denyCertificateRequest(requestId, denyReason)
      setDenyReason(null)
      ref.current.value = '';
    }
  }

  return <div className={styles.reasonDiv}>
    <div className={styles.reasonInput}>
      <label htmlFor="denyResaon">Denial reason:</label>
      <input id="denyResaon" name="denyResaon" ref={ref} onChange={handleDenyReason} />
    </div>
    <div className={styles.denyBtn} onClick={handleDenial}>
      <div>
        <a>Deny</a>
      </div>
      <div className={styles.imgDiv}>
        <Image src="/images/denyCertificateIcon.png" width={24} height={24} alt="denyCertificateIcon"></Image>
      </div>
    </div>
  </div>

}

async function denyCertificateRequest(requestId, reason) {
  const requestOptions = {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ reason: reason })
  };

  const response = await fetch(`${backUrl}/api/certificate/deny/${requestId}`, requestOptions);
  if (response.status == 200) {
    alert("Succesfully denied certificate.")
  } else {
    alert("Something went wrong!")
  }
}


function RequestCertificateAction({ issuerSerialNumber }) {
  const [selectedType, setSelectedType] = useState('INTERMEDIATE')
  const possibleCertificateTypes = ['INTERMEDIATE', 'END']

  function changedRequestType(event) {
    setSelectedType(event.target.value)
  }

  return (<div>
    <p className={styles.certInfoLabel}>Request certificate</p>
    <div className={styles.requestCert}>
      <select className={styles.requestType} name="certType" id="certType" onChange={changedRequestType}>
        {possibleCertificateTypes.map(type => <option id={type} value={type}>{type}</option>)}
      </select>
      <div className={styles.requestBtn} onClick={() => { requestNewCertificate(userId, selectedType, issuerSerialNumber) }}>
        <div>
          <a>Request</a>
        </div>
        <div className={styles.imgDiv}>
          <Image src="/images/requestCertificateIcon.png" width={24} height={24} alt="requestCertificateIcon"></Image>
        </div>
      </div>
    </div>
  </div>);
}

async function requestNewCertificate(userId, certificateType, issuerCertificateId) {
  const requestOptions = {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      issuerCertificateId: issuerCertificateId,
      certificateType: certificateType
    })
  };

  const response = await fetch(`${backUrl}/api/certificate/request/user/${userId}`, requestOptions);
  if (response.status.isError) {
    alert("Error in request")
  } else {
    const data = await response.json();
    alert(data.status)
  }
}

function InvalidateButton({ serialNumber }) {
  const [invalidationReason, setInvalidationReason] = useState(null)
  const ref = useRef(null)

  function handleInvalidationReason(event) {
    setInvalidationReason(event.target.value)
  }

  function handleInvalidation() {
    if (invalidationReason == null) {
      alert("You must enter a reason")
    } else {
      invalidateCertificateRequest(serialNumber, invalidationReason)
      setInvalidationReason(null)
      ref.current.value = '';
    }
  }
  return <div className={styles.reasonDiv}>
    <div className={styles.reasonInput}>
      <label htmlFor="invalidateReason">Invalidation reason:</label>
      <input id="invalidateReason" name="invalidateReason" ref={ref} onChange={handleInvalidationReason} />
    </div>
    <div className={styles.accentBtn} onClick={handleInvalidation}>
      <div>
        <a>Invalidate</a>
      </div>
      <div className={styles.imgDiv}>
        <Image src="/images/invalidateIcon.png" width={24} height={24} alt="invalidateIcon"></Image>
      </div>
    </div>
  </div>
}

async function invalidateCertificateRequest(serialNumber, reason) {
  const requestOptions = {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ reason: reason })
  };

  const response = await fetch(`${backUrl}/api/certificate/withdraw/${serialNumber}`, requestOptions);
  if (response.status == 200) {
    alert("Succesfully withdrawn certificate.")
  } else {
    alert("You can invalidate already invalidated certificate!")
  }
}

function DownloadButton({ serialNumber }) {
  return <div className={styles.accentBtn} onClick={() => downloadCertificate(serialNumber)}>
    <a>Download</a>
    <div className={styles.imgDiv}>
      <Image src="/images/downloadCertificateIcon.png" width={24} height={24} alt="downloadIcon"></Image>
    </div>
  </div>
}

async function downloadCertificate(serialNumber) {
  const requestOptions = {
    method: 'GET',
    headers: { 'Content-Type': 'application/json' }
  };

  const response = await fetch(`${backUrl}/api/certificate/download-certificate/${serialNumber}`, requestOptions);
  if (response.status.isError) {
    alert("Error in request")
  } else {
    const data = await response.json();
    const certData = atob(data.certificateContent);
    const fileBlob = new Blob([certData], { type: 'application/octet-stream' }); // Create a Blob object from the byte array
    const fileURL = URL.createObjectURL(fileBlob); // Create a URL for the Blob object

    const link = document.createElement('a');
    link.href = fileURL;
    link.download = `${serialNumber}.crt`;
    document.body.appendChild(link);
    link.click();
  }

}


