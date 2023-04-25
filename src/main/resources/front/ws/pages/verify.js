import { useState } from 'react';
import styles from '@/styles/Verify.module.css'
import PageContainer from '@/components/pageContainer';
import { backUrl } from "@/components/pageContainer";

export default function Verify() {
    return (<PageContainer>
        <VerifyPage />
    </PageContainer>);
}

function VerifyPage() {
    const [selectedSection, setSelectedSection] = useState(0);

    return (<div className={styles.main_container}>
        <ToggleOptions selectedSection={selectedSection} setSelectedSection={setSelectedSection} />
        <MainArea selectedSection={selectedSection} />
    </div>);
};

function ToggleOptions({ selectedSection, setSelectedSection }) {
    return (
        <div className={styles.toggle_options}>
            <p onClick={() => setSelectedSection(0)} className={selectedSection == 0 ? styles.selected : ''}>Verify by id</p>
            <div className={styles.separator}></div>
            <p onClick={() => setSelectedSection(1)} className={selectedSection == 1 ? styles.selected : ''}>Upload to verify</p>
        </div>
    );
}

function MainArea({ selectedSection }) {
    let tableToRender = null;
    if (selectedSection == 0) {
        tableToRender = <ById />
    } else if (selectedSection == 1) {
        tableToRender = <ByUpload />
    }
    return (
        <div className={styles.card} id="main_area">
            {tableToRender}
        </div>
    );
}

function ById() {

    // 0 - invalid / 1 - valid / -1 - doesn't exists
    const [certificateStatus, setCertificateStatus] = useState(null);

    async function checkIfValid(event) {
        event.preventDefault();

        if (!event.target.by_id.value) return;

        fetch(`${backUrl}/api/certificate/verify/${event.target.by_id.value}`)
            .then(response => { if (!response.ok) { return setCertificateStatus(-1); } return response.json() })
            .then(status => status['status'] ? setCertificateStatus(1) : setCertificateStatus(0))
            .catch(err => setCertificateStatus(-1));
        // event.target.reset();
    }

    return (<>
        <div className={styles.verifySection}>
            <form onSubmit={checkIfValid}>
                <label htmlFor="by_id">Insert ceritificate serial number:</label>
                <input className={styles.input_field} id='by_id' name='by_id' type='text' placeholder='' />
                <input className={styles.accentBtn} type='submit' value='Verify' />
            </form>
        </div>
        {certificateStatus !== null && <div className={styles.statusTitle}>
            <p className={certificateStatus == 1 ? styles.valid : styles.invalid}>{certificateStatus == 1 ? 'Valid' : certificateStatus == 0 ? 'Invalid' : 'Certificate with that id does not exist'}</p>
        </div>}
    </>

    );
}

function ByUpload() {
    // 0 - invalid / 1 - valid / -1 - doesn't exists
    const [certificateStatus, setCertificateStatus] = useState(null);
    const reader = new FileReader();

    async function checkIfValid(event) {
        event.preventDefault();
        const file = event.target.by_upload.files[0];
        reader.addEventListener('load', (event) => {
            const content = new Uint8Array(event.target.result);
            sendFile(content)
        });
        reader.readAsArrayBuffer(file);
    }

    function sendFile(fileContent) {
        fetch(`${backUrl}/api/certificate/verify/file`, {
            method: 'POST',
            body: fileContent,
            headers: {
                'Content-Type': 'application/octet-stream'
            }
        })
            .then(response => { if (!response.ok) { return setCertificateStatus(-1); } return response.json() })
            .then(status => status['status'] ? setCertificateStatus(1) : setCertificateStatus(0))
            .catch(err => setCertificateStatus(-1));
    }

    return (<>
        <div className={styles.verifySection}>
            <form onSubmit={checkIfValid}>
                <label htmlFor="by_upload">Upload ceritificate:</label>
                <input className={styles.input_field} id='by_upload' name='by_upload' type='file' accept='.crt' />
                <input className={styles.accentBtn} type='submit' value='Verify' />
            </form>
        </div>
        {certificateStatus !== null && <div className={styles.statusTitle}>
            <p className={certificateStatus == 1 ? styles.valid : styles.invalid}>{certificateStatus == 1 ? 'Valid' : certificateStatus == 0 ? 'Invalid' : 'Certificate file invalid'}</p>
        </div>}
    </>

    );
}